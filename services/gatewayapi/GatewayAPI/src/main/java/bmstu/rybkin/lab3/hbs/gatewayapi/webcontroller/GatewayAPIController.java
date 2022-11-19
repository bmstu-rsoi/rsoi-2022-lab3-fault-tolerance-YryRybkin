package bmstu.rybkin.lab3.hbs.gatewayapi.webcontroller;

import bmstu.rybkin.lab3.hbs.gatewayapi.models.*;
import bmstu.rybkin.lab3.hbs.gatewayapi.service.GatewayAPIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping()
@Tag(name = "@Gateway API")
public class GatewayAPIController {

    private final GatewayAPIService gatewayAPIService;

    private static final String BASE_URL = "/api/v1";

    private final String LOYALTYBASEURL;

    private final String PAYMENTBASEURL;

    private final String RESERVATIONBASEURL;
    private static final String X_USER_NAME = "X-User-Name";

    public GatewayAPIController( GatewayAPIService gatewayAPIService,
            @Value("${loyaltybaseurl}") String loyaltybaseurl,
            @Value("${paymentbaseurl}") String paymentbaseurl,
            @Value("${reservationbaseurl}") String reservationbaseurl
    )
    {

        this.gatewayAPIService = gatewayAPIService;
        LOYALTYBASEURL = loyaltybaseurl;
        PAYMENTBASEURL = paymentbaseurl;
        RESERVATIONBASEURL = reservationbaseurl;

    }

    //------------------------- Loyalty Service --------------------------------------------

    @Operation(summary = "Get Loyalty Info", operationId = "getLoyaltyInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loyalty Info",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoyaltyInfoResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found Loyalty for Username",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(BASE_URL + "/loyalty")
    public ResponseEntity<String> getLoyaltyInfo(@RequestHeader(X_USER_NAME) String username) {

        return gatewayAPIService.getLoyaltyInfo(username);

    }

    @Operation(summary = "Increment reservation count by 1", operationId = "incrementLoyalty")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loyalty Info",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoyaltyInfoResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found Loyalty for Username",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(BASE_URL + "/loyalty")
    public ResponseEntity<String> incrementLoyalty(@RequestHeader(X_USER_NAME) String username) {

        return gatewayAPIService.incrementLoyalty(username);

    }

    @Operation(summary = "Decrement reservation count by 1", operationId = "decrementLoyalty")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loyalty Info"),
            @ApiResponse(responseCode = "404", description = "Not found Loyalty for Username",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Illegal modification",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping(BASE_URL + "/loyalty")
    public ResponseEntity<String> decrementLoyalty(@RequestHeader(X_USER_NAME) String username) {

        return gatewayAPIService.decrementLoyalty(username);

    }


    //------------------------- Payment Service --------------------------------------------

    @Operation(summary = "Post new payment", operationId = "postPayment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new Payment",
                    headers = { @Header(name = "Location", description = "Path to new Payment") }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @PostMapping(BASE_URL + "/payment")
    public ResponseEntity<String> postPayment(@RequestParam UUID paymentUid, @RequestParam Integer price) {

        return gatewayAPIService.postPayment(paymentUid, price);

    }

    @Operation(summary = "Get Payment by UUID", operationId = "getPayment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment for UUID",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentInfo.class)) }),
            @ApiResponse(responseCode = "404", description = "Not found payment for UUID",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @GetMapping(BASE_URL + "/payment/{paymentUid}")
    public ResponseEntity<String> getPayment(@PathVariable UUID paymentUid) {

        return gatewayAPIService.getPayment(paymentUid);

    }

    @Operation(summary = "Cancel Payment by UUID", operationId = "cancelPayment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment for UUID was canceled"),
            @ApiResponse(responseCode = "404", description = "Not found uncanceled payment for UUID",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @DeleteMapping(BASE_URL + "/payment/{paymentUid}")
    public ResponseEntity<String> cancelPayment(@PathVariable UUID paymentUid) {

        return gatewayAPIService.cancelPayment(paymentUid);

    }


    //------------------------- Reservation Service ----------------------------------------

    @Operation(summary = "Get Hotels on page", operationId = "getHotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels on page",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaginationResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Page out of range",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @GetMapping(BASE_URL + "/hotels")
    public ResponseEntity<String> getHotels(@RequestParam Integer page, @RequestParam Integer size) {

        return gatewayAPIService.getHotels(page, size);

    }

    @Operation(summary = "Get all Reservations for user", operationId = "getReservations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations for user",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))) })
    })
    @GetMapping(BASE_URL + "/reservations")
    public List<ReservationResponse> getReservations(@RequestHeader("X-User-Name") String username) {

        return gatewayAPIService.getReservations(username);

    }

    @Operation(summary = "Get Reservation for user by UUID", operationId = "getReservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation with UUID",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaginationResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Reservation with UUID not found for user",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @GetMapping(BASE_URL + "/reservations/{reservationUid}")
    public ReservationResponse getReservation(@PathVariable UUID reservationUid,
                                          @RequestHeader("X-User-Name") String username) {

        return gatewayAPIService.getReservation(reservationUid, username);

    }

    @Operation(summary = "Create reservation for user", operationId = "postReservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation details",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaginationResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationErrorResponse.class)) })
    })
    @PostMapping(BASE_URL + "/reservations")
    public CreateReservationResponse postReservation(@RequestHeader("X-User-Name") String username,
                                                     @Valid @RequestBody CreateReservationRequest createReservationRequest) {

        return gatewayAPIService.postReservation(username, createReservationRequest);

    }

    @Operation(summary = "Cancel Reservation for user by UUID", operationId = "cancelReservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reservation with UUID was canceled",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaginationResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Reservation with UUID not found for user",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(BASE_URL + "/reservations/{reservationUid}")
    public void cancelReservation(@PathVariable UUID reservationUid,
                           @RequestHeader("X-User-Name") String username) {

        gatewayAPIService.cancelReservation(reservationUid, username);

    }

    //------------------------------- Aggregate --------------------------------------------

    @Operation(summary = "Get all information about user", operationId = "getMe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations for user",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserInfoResponse.class)) })
    })
    @GetMapping(BASE_URL + "/me")
    public UserInfoResponse getMe(@RequestHeader("X-User-Name") String username) {

        return gatewayAPIService.getMe(username);

    }

    @Operation(summary = "Get service state", operationId = "manageHealth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service available")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/manage/health")
    void manageHealth() {


    }

}
