package com.bezman.controller;

import com.bezman.annotation.PreAuthorization;
import com.bezman.model.User;
import com.bezman.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/v1/migration")
public class MigrationController extends BaseController {

    private MigrationService migrationService;

    @Autowired
    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/migrate_primary_o_auth_to_connected_account")
    public ResponseEntity migrate_primary_o_auth_to_connected_account() {

        migrationService.migrate_primary_o_auth_to_connected_account();

        return new ResponseEntity("OK", HttpStatus.OK);
    }

}
