package bmstu.rybkin.lab3.hbs.loyaltyapplication.service;

import bmstu.rybkin.lab3.hbs.loyaltyapplication.models.LoyaltyInfoResponse;

public interface LoyaltyService {

    LoyaltyInfoResponse getLoyaltyInfo(String username);
    LoyaltyInfoResponse increaseLoyalty(String username);
    LoyaltyInfoResponse decreaseLoyalty(String username);

}
