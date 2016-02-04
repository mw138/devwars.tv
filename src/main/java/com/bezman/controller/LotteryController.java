package com.bezman.controller;

import com.bezman.annotation.AuthedUser;
import com.bezman.model.User;
import com.bezman.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/v1/lottery/")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    @RequestMapping("/purchase")
    public ResponseEntity purchaseTickets(@RequestParam("count") Integer count, @AuthedUser User user) { //Stupid
        int price = 100;

        if (user.getRanking().getPoints() >= count * price) {
            lotteryService.purchaseLotteryTicketsForUser(user, count);
            return new ResponseEntity("Successfully Purchased Lottery Tickets", HttpStatus.OK);
        }
        return new ResponseEntity("Not Enough Devbits To Purchase Tickets", HttpStatus.CONFLICT);
    }

}
