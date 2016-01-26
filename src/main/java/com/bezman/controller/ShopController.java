package com.bezman.controller;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.PreAuthorization;
import com.bezman.init.DatabaseManager;
import com.bezman.model.ShopItem;
import com.bezman.model.User;
import com.bezman.service.ShopService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/v1/shop")
public class ShopController {

    private ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/purchase/custom_avatar")
    public ResponseEntity purchaseCustomAvatar(HttpServletRequest request, HttpServletResponse response) {
        ShopItem shopItem = getShopItemByName("Custom Avatar");

        User user = (User) request.getAttribute("user");

        if (shopItem != null) {
            if (user.canBuyItem(shopItem)) {
                user.getInventory().setAvatarChanges(user.getInventory().getAvatarChanges() + 1);

                shopService.purchaseItemForUser(shopItem, user);

                return new ResponseEntity("Purchased " + shopItem.getName(), HttpStatus.OK);
            } else {
                return new ResponseEntity("Not enough to buy item", HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity("Item not found", HttpStatus.NOT_FOUND);
        }
    }

    private ShopItem getShopItemByName(String name) {
        ShopItem returnItem = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from ShopItem where name = :name");
        query.setString("name", name);

        returnItem = (ShopItem) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return returnItem;
    }
}
