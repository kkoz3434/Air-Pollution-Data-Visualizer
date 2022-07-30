package pl.edu.agh.apdvbackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.apdvbackend.models.body_models.Response;
import pl.edu.agh.apdvbackend.models.body_models.group.AboutGroupResponseBody;
import pl.edu.agh.apdvbackend.models.body_models.group.AddGroupRequestBody;
import pl.edu.agh.apdvbackend.models.body_models.group.AdminPanelGroupResponseBody;
import pl.edu.agh.apdvbackend.models.body_models.group.EndpointGroupRequestBody;
import pl.edu.agh.apdvbackend.models.body_models.group.GroupMembersResponseBody;
import pl.edu.agh.apdvbackend.models.body_models.group.ShortGroupResponseBody;
import pl.edu.agh.apdvbackend.models.body_models.user.ShortUserResponseBody;
import pl.edu.agh.apdvbackend.services.GroupService;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Tag(name = "Group")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Get id and name info about all group")
    @GetMapping("/all/info")
    public Response<List<ShortGroupResponseBody>> getAllGroupsInfo() {
        return groupService.getAllGroupsInfo();
    }

    // TODO: Split this endpoint for two separate: one for members in group and one for endpoint
    @Operation(summary = "Get information about group")
    @GetMapping
    public Response<AboutGroupResponseBody> getGroupInfo(
            @RequestParam Long groupId) {
        return groupService.getGroupInfo(groupId);
    }

    @Operation(summary = "Get information about members in the group")
    @GetMapping("/members")
    public Response<GroupMembersResponseBody> getGroupMembers(
            @RequestParam Long groupId) {
        return groupService.getGroupMembers(groupId);
    }

    @Operation(summary = "Add user to group")
    @PostMapping("/user/add")
    public Response<AboutGroupResponseBody> addUserToGroup(
            @RequestParam Long groupId, @RequestParam Long userId) {
        return groupService.addUserToGroup(groupId, userId);
    }

    @Operation(summary = "Remove user from group")
    @DeleteMapping("/user/remove")
    public Response<AboutGroupResponseBody> removeUserFromGroup(
            @RequestParam Long groupId, @RequestParam Long userId) {
        return groupService.removeUserFromGroup(groupId, userId);
    }

    @Operation(summary = "Remove group")
    @DeleteMapping
    public void removeGroup(@RequestParam Long groupId) {
        groupService.removeGroup(groupId);
    }

    @Operation(summary = "Change enable endpoints for group")
    @PutMapping("/endpoints")
    public Response<AboutGroupResponseBody> changeEnableEndpoints(@RequestBody
                                                                  List<EndpointGroupRequestBody> endpointGroupRequestBodyList,
                                                                  @RequestParam
                                                                  Long groupId) {
        return groupService.changeEnableEndpoints(
                endpointGroupRequestBodyList, groupId);
    }

    @Operation(summary = "Add new group")
    @PostMapping
    public Response<AboutGroupResponseBody> addGroup(
            @RequestBody AddGroupRequestBody addGroupRequestBody) {
        return groupService.addGroup(addGroupRequestBody);
    }

    @Operation(summary = "Get all users that are not in group")
    @GetMapping("/users/not/in")
    public Response<List<ShortUserResponseBody>> getNotMembersOfTheGroup(
            @RequestParam Long groupId
    ) {
        return groupService.getNotMembersOfTheGroup(groupId);
    }

    @Operation(summary = "Get information about what endpoints and fields (non) belongs to group")
    @GetMapping("endpoints")
    public Response<AdminPanelGroupResponseBody> getAdminPanelGroup(
            @RequestParam Long groupId
    ) {
        return groupService.getAdminPanelGroup(groupId);
    }
}
