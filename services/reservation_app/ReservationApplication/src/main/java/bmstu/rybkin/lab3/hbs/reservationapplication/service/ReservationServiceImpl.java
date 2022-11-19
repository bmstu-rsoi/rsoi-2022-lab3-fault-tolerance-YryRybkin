package bmstu.rybkin.lab3.hbs.reservationapplication.service;

import bmstu.rybkin.lab3.hbs.reservationapplication.jpa.Hotels;
import bmstu.rybkin.lab3.hbs.reservationapplication.jpa.HotelsRepo;
import bmstu.rybkin.lab3.hbs.reservationapplication.jpa.Reservation;
import bmstu.rybkin.lab3.hbs.reservationapplication.models.*;
import bmstu.rybkin.lab3.hbs.reservationapplication.jpa.ReservationRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import javax.persistence.EntityNotFoundException;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final HotelsRepo hotelsRepo;
    private final ReservationRepo reservationRepo;

    public ReservationServiceImpl(HotelsRepo hotelsRepo,
                                  ReservationRepo reservationRepo) {

        this.hotelsRepo = hotelsRepo;
        this.reservationRepo = reservationRepo;

    }

    @Transactional(readOnly = true)
    @Override
    public PaginationResponse getHotels(Integer page, Integer size) {

        List<HotelResponse> hotels = hotelsRepo.findAll().stream().map(
                this::buildHotelResponse).toList();
        int skip = size*(page - 1);
        if (skip > hotels.size())
            throw new NotFoundException(String.format("Page number: %d not found", page));
        return buildPaginationResponse(page, size, hotels.size(),
                hotels.subList(skip, Math.min((skip + size), hotels.size())));

    }


    @Transactional(readOnly = true)
    @Override
    public List<ResServResponse> getReservations(String username) {

        List<Reservation> reservations = reservationRepo.findAllByUsername(username);
        List<ResServResponse> resps = new ArrayList<>();
        for (Reservation reservation : reservations)
        {

            Hotels hotel = hotelsRepo.findById(reservation.getHotelId()).orElse(null);
            HotelInfo hotelInfo;
            if (hotel != null)
            {

                String fullAddress = String.join(", ", hotel.getCountry(),
                        hotel.getCity(), hotel.getAddress());
                hotelInfo = buildHotelInfo(hotel.getHotelUid(), hotel.getName(),
                        fullAddress, hotel.getStars());

            }
            else {

                hotelInfo = null;

            }

            resps.add(buildReservationServiceResponse(reservation.getReservationUid(), hotelInfo,
                    reservation.getStartDate(), reservation.getEndDate(), reservation.getStatus(),
                    reservation.getPaymentUid()));

        }
        return resps;

    }


    @Transactional(readOnly = true)
    @Override
    public ResServResponse getReservation(UUID reservationUid, String username) {

        Reservation reservation = reservationRepo.findByReservationUidAndUsername(
                reservationUid, username).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Reservation with uid: %s not found for user: %s",
                                reservationUid.toString(), username)) );
        Hotels hotel = hotelsRepo.findById(reservation.getHotelId()).orElseThrow(
                () -> new EntityNotFoundException("Could not find reserved hotel") );
        HotelInfo hotelInfo;
        String fullAddress = String.join(", ", hotel.getCountry(),
                hotel.getCity(), hotel.getAddress());
        hotelInfo = buildHotelInfo(hotel.getHotelUid(), hotel.getName(),
                fullAddress, hotel.getStars());

        return buildReservationServiceResponse(reservation.getReservationUid(), hotelInfo, reservation.getStartDate(),
                reservation.getEndDate(), reservation.getStatus(), reservation.getPaymentUid());

    }


    @Transactional
    @Override
    public CrResServResponse postReservation(String username,
                                             CreateReservationRequest createReservationRequest) {

        Hotels hotel = hotelsRepo.findByHotelUid(createReservationRequest.getHotelUid()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Could not find hotel with uid: %s",
                        createReservationRequest.getHotelUid())));

        long diff = ChronoUnit.DAYS.between(createReservationRequest.getStartDate().toInstant().
                        atZone(ZoneId.systemDefault()).toLocalDate(),
                createReservationRequest.getEndDate().toInstant().
                        atZone(ZoneId.systemDefault()).toLocalDate());
        int price = Math.toIntExact(diff) * hotel.getPrice();

        UUID paymentUid = UUID.randomUUID();

        Reservation reservation = new Reservation(UUID.randomUUID(), username, paymentUid, hotel.getId(),
                "PAID", createReservationRequest.getStartDate(), createReservationRequest.getEndDate());
        reservationRepo.save(reservation);

        return buildCreateReservationServiceResponse(reservation.getReservationUid(), hotel.getHotelUid(), reservation.getStartDate(),
                reservation.getEndDate(), reservation.getStatus(), reservation.getPaymentUid(), price);

    }

    @Transactional
    @Override
    public void cancelReservation(UUID reservationUid, String username) {

        Reservation reservation = reservationRepo.findByReservationUidAndUsername(
                reservationUid, username).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Reservation with uid: %s not found for user: %s",
                                reservationUid.toString(), username)) );

        reservation.setStatus("CANCELED");
        reservationRepo.save(reservation);

    }

    @Transactional
    @Override
    public void rollbackCancellation(UUID reservationUid, String username) {

        Reservation reservation = reservationRepo.findByReservationUidAndUsername(
                reservationUid, username).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Reservation with uid: %s not found for user: %s",
                                reservationUid.toString(), username)) );

        reservation.setStatus("PAID");
        reservationRepo.save(reservation);

    }

    @Transactional
    @Override
    public void deleteReservation(UUID reservationUid, String username) {

        reservationRepo.deleteReservationByReservationUidAndUsername(reservationUid, username);

    }

    private HotelResponse buildHotelResponse(Hotels hotel) {

        return new HotelResponse(hotel.getHotelUid(), hotel.getName(), hotel.getCountry(),
                    hotel.getCity(), hotel.getAddress(), hotel.getStars(), hotel.getPrice());

    }

    private PaginationResponse buildPaginationResponse(Integer page, Integer size, Integer total, List<HotelResponse> hotels) {

        return new PaginationResponse(page, size, total, hotels);

    }

    private HotelInfo buildHotelInfo(UUID hotelUid, String name, String fullAddress, Integer stars) {

        return new HotelInfo(hotelUid, name, fullAddress, stars);

    }

    private ResServResponse buildReservationServiceResponse(UUID reservationUid, HotelInfo hotelInfo,
                                                            Date startDate, Date endDate, String status,
                                                            UUID paymentUid) {

        return new ResServResponse(reservationUid, hotelInfo, startDate, endDate, status, paymentUid);

    }


    private CrResServResponse buildCreateReservationServiceResponse(UUID reservationUid, UUID hotelUid,
                                                                    Date startDate, Date endDate,
                                                                    String status, UUID paymentUid,
                                                                    Integer price) {

        return  new CrResServResponse(reservationUid, hotelUid, startDate, endDate, status, paymentUid, price);

    }

}
