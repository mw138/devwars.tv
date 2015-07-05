package com.bezman.controller;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.BaseModel;
import com.bezman.model.ObjectiveItem;
import com.bezman.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Terence on 5/5/2015.
 */
@Controller
@RequestMapping("/v1/objective")
public class ObjectiveItemController extends BaseController
{

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/create")
    public ResponseEntity createObjective(HttpServletRequest request, HttpServletResponse response,
                                          @RequestParam("objective") String objective)
    {
        ObjectiveItem objectiveItem = new ObjectiveItem(objective);

        DatabaseUtil.saveObjects(true, objectiveItem);

        return new ResponseEntity(Reference.gson.toJson(objectiveItem), HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}")
    public ResponseEntity getObjective(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable("id") int id)
    {
        ObjectiveItem objectiveItem = (ObjectiveItem) BaseModel.byID(ObjectiveItem.class, id);

        if (objectiveItem != null)
        {
            return new ResponseEntity(objectiveItem.toJSON(), HttpStatus.OK);
        } else
        {
            return new ResponseEntity("Objective Not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/{id}/delete")
    public ResponseEntity deleteObjective(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable("id") int id)
    {
        BaseModel objectiveItem = BaseModel.byID(ObjectiveItem.class, id);

        if (objectiveItem != null)
        {
            DatabaseUtil.deleteObjects(objectiveItem);
            return new ResponseEntity(objectiveItem.toJSON(), HttpStatus.OK);
        } else
        {
            return new ResponseEntity("Objective Not found", HttpStatus.NOT_FOUND);
        }
    }

}
