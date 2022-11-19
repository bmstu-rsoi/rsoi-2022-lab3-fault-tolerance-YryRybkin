package bmstu.rybkin.lab3.hbs.gatewayapi.service;

import bmstu.rybkin.lab3.hbs.gatewayapi.faulttolerance.CircuitBreaker;
import bmstu.rybkin.lab3.hbs.gatewayapi.faulttolerance.ScheduleRunner;
import bmstu.rybkin.lab3.hbs.gatewayapi.faulttolerance.State;
import bmstu.rybkin.lab3.hbs.gatewayapi.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.awt.desktop.SystemEventListener;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class GatewayAPIServiceImplementation implements GatewayAPIService {

    private ScheduleRunner scheduleRunner;
    private final CircuitBreaker loyaltyCircuitBreaker;
    private final CircuitBreaker paymentCircuitBreaker;
    private final CircuitBreaker reservationCircuitBreaker;
    private final String LOYALTYBASEURL;

    private final String PAYMENTBASEURL;

    private final String RESERVATIONBASEURL;
    private static final String X_USER_NAME = "X-User-Name";

    private final RestTemplate restTemplate= new RestTemplate();

    public GatewayAPIServiceImplementation(
            @Value("${loyaltybaseurl}") String loyaltybaseurl,
            @Value("${paymentbaseurl}") String paymentbaseurl,
            @Value("${reservationbaseurl}") String reservationbaseurl,
            @Value("${loyaltymanage}") String loyaltymanage,
            @Value("${paymentmanage}") String paymentmanage,
            @Value("${reservationmanage}") String reservationmanage
    ) {

        LOYALTYBASEURL = loyaltybaseurl;
        PAYMENTBASEURL = paymentbaseurl;
        RESERVATIONBASEURL = reservationbaseurl;


        loyaltyCircuitBreaker = new CircuitBreaker(10, 10000,
                new TimerTask() {
                    @Override
                    public void run() {
                        loyaltyCircuitBreaker.setState(State.HALF_OPEN);

                        ResponseEntity<String> response;
                        try {
                            response = noBodyRestTemplateExchangeLoyalty(loyaltymanage, new HttpHeaders(), HttpMethod.GET);
                        } catch (Exception e) {
                            System.out.println(e);
                            loyaltyCircuitBreaker.requestFailure();
                            throw e;
                        }

                        if (response.getStatusCode() == HttpStatus.OK)
                            loyaltyCircuitBreaker.requestSuccess();
                        else
                            loyaltyCircuitBreaker.requestFailure();

                    }
                });

        paymentCircuitBreaker = new CircuitBreaker(10, 10000,
                new TimerTask() {
                    @Override
                    public void run() {

                        paymentCircuitBreaker.setState(State.HALF_OPEN);

                        ResponseEntity<String> response;
                        try {
                            response = noBodyRestTemplateExchangePayment(paymentmanage, HttpMethod.GET);
                        } catch (Exception e) {
                            System.out.println(e);
                            paymentCircuitBreaker.requestFailure();
                            throw e;
                        }

                        if (response.getStatusCode() == HttpStatus.OK)
                            paymentCircuitBreaker.requestSuccess();
                        else
                            paymentCircuitBreaker.requestFailure();

                    }
                });


        reservationCircuitBreaker = new CircuitBreaker(10, 10000,
                new TimerTask() {
                    @Override
                    public void run() {
                        reservationCircuitBreaker.setState(State.HALF_OPEN);

                        ResponseEntity<String> response;
                        try {
                            response = restTemplateExchangeReservation(reservationmanage, new HttpEntity<>(new HttpHeaders()), HttpMethod.GET);
                        } catch (Exception e) {
                            System.out.println(e);
                            reservationCircuitBreaker.requestFailure();
                            throw e;
                        }

                        if (response.getStatusCode() == HttpStatus.OK)
                            reservationCircuitBreaker.requestSuccess();
                        else
                            reservationCircuitBreaker.requestFailure();

                    }
                });

    }

    @Override
    public ResponseEntity<String> getLoyaltyInfo(String username) {

        String resourceUrl = LOYALTYBASEURL + "/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        return noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.GET);

    }

    @Override
    public ResponseEntity<String> incrementLoyalty(String username) {

        String resourceUrl = LOYALTYBASEURL + "/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        return noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.POST);

    }

    @Override
    public ResponseEntity<String> decrementLoyalty(String username) {

        String resourceUrl = LOYALTYBASEURL + "/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        return noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.DELETE);

    }

    @Override
    public ResponseEntity<String> postPayment(UUID paymentUid, Integer price) {

        String resourceUrl = PAYMENTBASEURL +
                String.format("?paymentUid=%s&price=%d", paymentUid.toString(), price);
        return noBodyRestTemplateExchangePayment(resourceUrl, HttpMethod.POST);

    }

    @Override
    public ResponseEntity<String> getPayment(UUID paymentUid) {

        String resourceUrl = PAYMENTBASEURL +
                String.format("/%s", paymentUid.toString());
        return noBodyRestTemplateExchangePayment(resourceUrl, HttpMethod.GET);

    }

    @Override
    public ResponseEntity<String> cancelPayment(UUID paymentUid) {

        String resourceUrl = PAYMENTBASEURL +
                String.format("/%s", paymentUid.toString());
        return noBodyRestTemplateExchangePayment(resourceUrl, HttpMethod.DELETE);

    }

    @Override
    public ResponseEntity<String> getHotels(Integer page, Integer size) {

        String resourceUrl = RESERVATIONBASEURL + "/hotels" +
                String.format("?page=%d&size=%d", page, size);
        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());
        return restTemplateExchangeReservation(resourceUrl, request, HttpMethod.GET);

    }

    @Override
    public List<ReservationResponse> getReservations(String username) {

        String resourceUrl = RESERVATIONBASEURL + "/reservations";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> reservationResponse = restTemplateExchangeReservation(resourceUrl, request, HttpMethod.GET);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ResServResponse> reservationResponses;
        try {
            reservationResponses = Arrays.asList(objectMapper.readValue(reservationResponse.getBody(), ResServResponse[].class));
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        List<ReservationResponse> resp = new ArrayList<>();
        for (ResServResponse response : reservationResponses) {

            PaymentInfo paymentInfo;
            try {
                paymentInfo = getPaymentInfo(response.getPaymentUid());
            } catch (HttpServerErrorException | HttpClientErrorException e) {

                System.out.println(e);
                paymentInfo = null;

            }
            resp.add(buildReservationResponse(response, paymentInfo));

        }
        return resp;

    }

    @Override
    public ReservationResponse getReservation(UUID reservationUid, String username) {

        String resourceUrl = RESERVATIONBASEURL + "/reservations" +
                String.format("/%s", reservationUid.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> reservationResponse = restTemplateExchangeReservation(resourceUrl, request, HttpMethod.GET);
        ObjectMapper objectMapper = new ObjectMapper();
        ResServResponse resp;
        try {
            resp = objectMapper.readValue(reservationResponse.getBody(), ResServResponse.class);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        PaymentInfo paymentInfo;
        try {
            paymentInfo = getPaymentInfo(resp.getPaymentUid());
        } catch (HttpServerErrorException e) {

            System.out.println(e);
            paymentInfo = null;

        }

        return buildReservationResponse(resp, paymentInfo);

    }

    @Override
    public CreateReservationResponse postReservation(String username,
                                                     CreateReservationRequest createReservationRequest) {

        String resourceUrl = RESERVATIONBASEURL + "/reservations";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        HttpEntity<CreateReservationRequest> request = new HttpEntity<>(createReservationRequest, headers);
        ResponseEntity<String> postReservationResponse = restTemplateExchangeReservation(resourceUrl, request, HttpMethod.POST);

        ObjectMapper objectMapper = new ObjectMapper();

        CrResServResponse reservationServiceResponse;
        try {
            reservationServiceResponse = objectMapper.readValue(postReservationResponse.getBody(), CrResServResponse.class);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        resourceUrl = LOYALTYBASEURL + "/me";
        headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        ResponseEntity<String> loyaltyResponse;
        try {
            loyaltyResponse = noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.GET);
        } catch (ResourceAccessException e) {
            throw new ResourceAccessException("Loyalty Service unavailable");
        } catch (Exception e) {

            rollbackReservation(reservationServiceResponse.getReservationUid(), username);
            throw e;

        }
        LoyaltyInfoResponse loyaltyInfoResponse;
        try {
            loyaltyInfoResponse = objectMapper.readValue(loyaltyResponse.getBody(), LoyaltyInfoResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Integer price = reservationServiceResponse.getPrice() * (100 - loyaltyInfoResponse.getDiscount()) / 100;

        resourceUrl = PAYMENTBASEURL +
                String.format("?paymentUid=%s&price=%d", reservationServiceResponse.getPaymentUid().toString(), price);
        try {
            noBodyRestTemplateExchangePayment(resourceUrl, HttpMethod.POST);
        } catch (Exception e) {

            rollbackReservation(reservationServiceResponse.getReservationUid(), username);
            throw e;

        }
        resourceUrl = LOYALTYBASEURL + "/me";
        headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        try {
            noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.POST);
        } catch (Exception e) {

            rollbackReservation(reservationServiceResponse.getReservationUid(), username);
            rollbackPayment(reservationServiceResponse.getPaymentUid());
            throw e;

        }

        PaymentInfo paymentInfo;
        try {
            paymentInfo = getPaymentInfo(reservationServiceResponse.getPaymentUid());
        } catch (Exception e) {

            rollbackReservation(reservationServiceResponse.getReservationUid(), username);
            rollbackPayment(reservationServiceResponse.getPaymentUid());
            noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.DELETE);
            throw e;

        }
        return buildCreateReservationResponse(reservationServiceResponse, loyaltyInfoResponse, paymentInfo);

    }

    @Override
    public void cancelReservation(UUID reservationUid, String username) {

        String resourceUrl = RESERVATIONBASEURL + "/reservations" +
                String.format("/%s", reservationUid.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> reservationResponse = restTemplateExchangeReservation(resourceUrl, request, HttpMethod.GET);
        ObjectMapper objectMapper = new ObjectMapper();
        ResServResponse resp;
        try {
            resp = objectMapper.readValue(reservationResponse.getBody(), ResServResponse.class);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        restTemplateExchangeReservation(resourceUrl, request, HttpMethod.DELETE);

        resourceUrl = PAYMENTBASEURL +
                String.format("/%s", resp.getPaymentUid().toString());
        ResponseEntity<String> deletePayment;

        try {
            noBodyRestTemplateExchangePayment(resourceUrl, HttpMethod.DELETE);
        } catch (Exception e) {

            resourceUrl = RESERVATIONBASEURL + "/reservations/cancel" +
                    String.format("/%s", reservationUid.toString());
            headers = new HttpHeaders();
            headers.set(X_USER_NAME, username);
            request = new HttpEntity<>(headers);
            restTemplateExchangeReservation(resourceUrl, request, HttpMethod.POST);
            throw e;

        }

        resourceUrl = LOYALTYBASEURL + "/me";
        headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        try {
            noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.DELETE);
        } catch (HttpServerErrorException e) {

            scheduleRunner = new ScheduleRunner(new TimerTask() {
                @Override
                public void run() {

                    String resourceUrl = LOYALTYBASEURL + "/me";
                    HttpHeaders headers = new HttpHeaders();
                    headers.set(X_USER_NAME, username);
                    try {
                        noBodyRestTemplateExchangeLoyalty(resourceUrl, headers, HttpMethod.DELETE);
                    } catch (HttpServerErrorException e) {

                        throw e;

                    }
                    scheduleRunner.taskComplete();

                }
            });

        }

    }

    @Override
    public UserInfoResponse getMe(String username) {

        List<ReservationResponse> reservationResponses;
        try {
            reservationResponses = getReservations(username);
        } catch (HttpServerErrorException | HttpClientErrorException e) {

            reservationResponses = null;
            System.out.println(e);

        } catch (ResourceAccessException e) {

            reservationResponses = null;
            System.out.println(e);

        }

        ResponseEntity<String> loyalty;
        LoyaltyInfoResponse loyaltyInfoResponse;

        try {
            loyalty = getLoyaltyInfo(username);
        } catch (HttpServerErrorException | HttpClientErrorException e) {

            System.out.println(e);
            return new UserInfoResponse(reservationResponses, null);

        } catch (ResourceAccessException e) {

            System.out.println(e);
            return new UserInfoResponse(reservationResponses, null);

        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            loyaltyInfoResponse = objectMapper.readValue(loyalty.getBody(), LoyaltyInfoResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new UserInfoResponse(reservationResponses, loyaltyInfoResponse);

    }

    private ResponseEntity<String> noBodyResTemplateExchange(String resourceUrl, HttpHeaders headers, HttpMethod httpMethod)
    {

        HttpEntity<?> request = new HttpEntity<>(headers);
        return restTemplate.exchange(
                resourceUrl,
                httpMethod,
                request,
                String.class
        );

    }

    private ResponseEntity<String> noBodyRestTemplateExchangeLoyalty(String resourceUrl, HttpHeaders headers, HttpMethod httpMethod)
    {

        if (loyaltyCircuitBreaker.getState() != State.CLOSED) {

            throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Loyalty Service unavailable");

        }

       HttpEntity<?> request = new HttpEntity<>(headers);
       ResponseEntity<String> response;
       try {
           response = restTemplate.exchange(
                   resourceUrl,
                   httpMethod,
                   request,
                   String.class
           );
       } catch (Exception e) {

           loyaltyCircuitBreaker.requestFailure();
           System.out.println(e);
           if ((e instanceof NoRouteToHostException) || (e instanceof UnknownHostException)) {
               throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Loyalty Service unavailable");
           }
           throw new RuntimeException(e.getMessage());

       }

       loyaltyCircuitBreaker.requestSuccess();
       return response;

    }


    private ResponseEntity<String> noBodyRestTemplateExchangePayment(String resourceUrl, HttpMethod httpMethod)
    {

        if (paymentCircuitBreaker.getState() != State.CLOSED) {

            throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Payment service is unavailable");

        }

        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    resourceUrl,
                    httpMethod,
                    request,
                    String.class
            );
        } catch (Exception e) {

            paymentCircuitBreaker.requestFailure();
            throw e;

        }

        paymentCircuitBreaker.requestSuccess();;
        return response;

    }

    private ResponseEntity<String> restTemplateExchangeReservation(String resourceUrl, HttpEntity<?> request, HttpMethod httpMethod)
    {

        if (reservationCircuitBreaker.getState() != State.CLOSED) {

            throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Reservation service is unavailable");

        }

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    resourceUrl,
                    httpMethod,
                    request,
                    String.class
            );
        } catch (Exception e) {

            reservationCircuitBreaker.requestFailure();
            throw e;

        }

        reservationCircuitBreaker.requestSuccess();
        return response;

    }

    private PaymentInfo getPaymentInfo(UUID paymenUid) {

        String resourceUrl = PAYMENTBASEURL +
                String.format("/%s", paymenUid.toString());
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response = noBodyRestTemplateExchangePayment(resourceUrl, HttpMethod.GET);

        ObjectMapper objectMapper = new ObjectMapper();
        PaymentInfo paymentInfo;
        try {
            paymentInfo = objectMapper.readValue(response.getBody(), PaymentInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return paymentInfo;

    }

    private ReservationResponse buildReservationResponse(ResServResponse response, PaymentInfo paymentInfo)
    {

        return new ReservationResponse(response.getReservationUid(), response.getHotel(), response.getStartDate(),
                                        response.getEndDate(), response.getStatus(), paymentInfo);

    }

    private CreateReservationResponse buildCreateReservationResponse(CrResServResponse crResServResponse,
                                                                     LoyaltyInfoResponse loyaltyInfoResponse,
                                                                     PaymentInfo paymentInfo) {

        return new CreateReservationResponse(crResServResponse.getReservationUid(), crResServResponse.getHotelUid(),
                crResServResponse.getStartDate(), crResServResponse.getEndDate(),
                loyaltyInfoResponse.getDiscount(), crResServResponse.getStatus(), paymentInfo);

    }

    private ResponseEntity<String> rollbackReservation(UUID reservationUid, String username) {

        String resourceurl = RESERVATIONBASEURL + "/reservations/cancel" +
                String.format("/%s", reservationUid.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_USER_NAME, username);
        return restTemplateExchangeReservation(resourceurl, new HttpEntity<>(headers), HttpMethod.DELETE);

    }

    private ResponseEntity<String> rollbackPayment(UUID paymentUid) {

        String resourceurl = PAYMENTBASEURL + "/cancel" +
                String.format("/%s", paymentUid.toString());
        return noBodyRestTemplateExchangePayment(resourceurl, HttpMethod.DELETE);

    }

}
