package bmstu.rybkin.lab3.hbs.reservationapplication.webcontroller;

import bmstu.rybkin.lab3.hbs.reservationapplication.models.*;
import bmstu.rybkin.lab3.hbs.reservationapplication.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping()
@Tag(name = "Reservation REST API operations")
public class ReservationController {

    private static final String BASE_URL = "/api/v1/services/reservation";
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {

        this.reservationService = reservationService;

    }

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
    PaginationResponse getHotels(@RequestParam Integer page, @RequestParam Integer size) {

        if ((page < 1) || (size < 1))
            throw new IllegalStateException("Bad request params: page and size must be greater than 0");
        return reservationService.getHotels(page, size);

    }

    //    UserInfoResponse getMe(String username);

    @Operation(summary = "Get all Reservations for user", operationId = "getReservations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations for user",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))) })
    })
    @GetMapping(BASE_URL + "/reservations")
    List<ResServResponse> getReservations(@RequestHeader("X-User-Name") String username) {

        return reservationService.getReservations(username);

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
    ResServResponse getReservation(@PathVariable UUID reservationUid,
                                   @RequestHeader("X-User-Name") String username) {

        return reservationService.getReservation(reservationUid, username);

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
    CrResServResponse postReservation(@RequestHeader("X-User-Name") String username,
                                      @Valid @RequestBody CreateReservationRequest createReservationRequest) {

        return reservationService.postReservation(username, createReservationRequest);

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
    void cancelReservation(@PathVariable UUID reservationUid,
                           @RequestHeader("X-User-Name") String username) {

        reservationService.cancelReservation(reservationUid, username);

    }

    @Operation(summary = "Cancel Reservation for user by UUID", operationId = "cancelReservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation with UUID was canceled",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaginationResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Reservation with UUID not found for user",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @PostMapping(BASE_URL + "/reservations/cancel/{reservationUid}")
    void rollbackCancelReservation(@PathVariable UUID reservationUid,
                           @RequestHeader("X-User-Name") String username) {

        reservationService.rollbackCancellation(reservationUid, username);

    }

    @Operation(summary = "Remove Reservation by UUID", operationId = "deleteReservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reservation for UUID was removed")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(BASE_URL + "/reservations/cancel/{reservationUid}")
    public void deletePayment(@PathVariable UUID reservationUid,
                             @RequestHeader("X-User-Name") String username) {

        reservationService.deleteReservation(reservationUid, username);

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
