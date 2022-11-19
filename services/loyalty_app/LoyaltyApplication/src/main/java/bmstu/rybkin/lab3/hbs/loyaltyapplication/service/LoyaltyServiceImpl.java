package bmstu.rybkin.lab3.hbs.loyaltyapplication.service;

import bmstu.rybkin.lab3.hbs.loyaltyapplication.jpa.Loyalty;
import bmstu.rybkin.lab3.hbs.loyaltyapplication.jpa.LoyaltyRepo;
import bmstu.rybkin.lab3.hbs.loyaltyapplication.models.LoyaltyInfoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyRepo loyaltyRepo;

    LoyaltyServiceImpl(LoyaltyRepo loyaltyRepo) {

        this.loyaltyRepo = loyaltyRepo;

    }

    @Transactional(readOnly = true)
    @Override
    public LoyaltyInfoResponse getLoyaltyInfo(String username) {

        Loyalty loyalty =  loyaltyRepo.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Loyalty member with name: %s not found", username)) );
        return buildLoyaltyResponse(loyalty);

    }

    @Transactional
    @Override
    public LoyaltyInfoResponse increaseLoyalty(String username) {

        Loyalty loyalty =  loyaltyRepo.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Loyalty member with name: %s not found", username)) );
        loyalty = modifyLoyaltyCount(loyalty, 1);
        loyaltyRepo.save(loyalty);
        return buildLoyaltyResponse(loyalty);

    }

    @Transactional
    @Override
    public LoyaltyInfoResponse decreaseLoyalty(String username) {

        Loyalty loyalty =  loyaltyRepo.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Loyalty member with name: %s not found", username)) );
        loyalty = modifyLoyaltyCount(loyalty, -1);
        loyaltyRepo.save(loyalty);
        return buildLoyaltyResponse(loyalty);

    }

    Loyalty modifyLoyaltyCount(Loyalty loyalty, Integer modifier) {

        Loyalty loyaltyInternal = new Loyalty(loyalty);
        int reservationCount = loyaltyInternal.getReservationCount() + modifier;
        if (reservationCount < 0) {

            throw new IllegalStateException("Tried to reduce Loyalty reservation count to less than zero");

        } else if (reservationCount < 10) {

            loyaltyInternal.setStatus("BRONZE");
            loyaltyInternal.setDiscount(5);

        } else if (reservationCount < 20) {

            loyaltyInternal.setStatus("SILVER");
            loyaltyInternal.setDiscount(7);

        } else {

            loyaltyInternal.setStatus("GOLD");
            loyaltyInternal.setDiscount(10);

        }
        loyaltyInternal.setReservationCount(reservationCount);
        return loyaltyInternal;


    }

    LoyaltyInfoResponse buildLoyaltyResponse(Loyalty loyalty) {

        return new LoyaltyInfoResponse(loyalty.getStatus(),
                                        loyalty.getDiscount(), loyalty.getReservationCount());

    }

}
