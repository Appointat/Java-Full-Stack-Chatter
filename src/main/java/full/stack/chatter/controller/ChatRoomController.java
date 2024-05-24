package full.stack.chatter.controller;

import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.model.User;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(value = "/chatroom")
public class ChatRoomController {
    @Resource
    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    @RequestMapping(value = "/createchatroom")
    public String createChatRoom(String email, Boolean is_admin, String chat_room_name, String description,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime create_date,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expire_date) {
        System.out.println(chat_room_name);
        System.out.println(description);
        System.out.println(email);
        System.out.println(is_admin);
        System.out.println(create_date);
        System.out.println(expire_date);
        // The input `creator` can be either an admin user or a normal user
        if(is_admin){
            // Set/create a chat room
            ChatRoom chat_room = new ChatRoom();
            AdminUser creator=userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
            chat_room.setChatRoom(chat_room_name, description, creator, create_date, expire_date);
            // Update the chat room in the database
            userAndRoomManagementRequest.addChatRoom(chat_room);
            // Update the creator, whose `created_chat_rooms` field should be updated
            creator.addCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateAdminUser(creator);
            return"redirect:/page_admin";
        }else{
            // Set/create a chat room
            ChatRoom chat_room = new ChatRoom();
            NormalUser creator=userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
            chat_room.setChatRoom(chat_room_name, description, creator, create_date, expire_date);
            // Update the chat room in the database
            userAndRoomManagementRequest.addChatRoom(chat_room);
            // Update the creator, whose `created_chat_rooms` field should be updated
            creator.addCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateNormalUser(creator);
            return"redirect:/page_normaluser";
        }

//        if (creator instanceof NormalUser normal_user) {
//            userAndRoomManagementRequest.updateNormalUser(normal_user);
//        } else if (creator instanceof AdminUser admin_user) {
//
//        } else {
//            throw new RuntimeException("Invalid user type");
//        }
    }

    @RequestMapping(value = "/chatroomslist")
    public List<ChatRoom> getChatRooms() {
        return userAndRoomManagementRequest.getChatRooms();
    }

    @RequestMapping(value = "/removechatroom") // TODO: to be tested
    public void removeChatRoom(Long chat_room_id) { // TODO: to add the input parameters to identify the chat room
        ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(chat_room_id);

        // Remove all the invited users in the chat room
        for (NormalUser _normal_user : chat_room.getNormalUsers()) { // remove the chat room id from the invited user's `invited_chat_rooms` field
            _normal_user.removeInvitedChatRoom(chat_room_id);
            userAndRoomManagementRequest.updateNormalUser(_normal_user);
        }
        for (AdminUser _admin_user : chat_room.getAdminUsers()) { // remove the chat room id from the invited user's `invited_chat_rooms` field
            _admin_user.removeInvitedChatRoom(chat_room_id);
            userAndRoomManagementRequest.updateAdminUser(_admin_user);
        }

        // Remove the creator who created the chat room
        if (chat_room.getCreator() instanceof NormalUser normal_user) {
            normal_user.removeCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateNormalUser(normal_user);
        } else if (chat_room.getCreator() instanceof AdminUser admin_user) {
            admin_user.removeCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateAdminUser(admin_user);
        } else {
            throw new RuntimeException("Cannot remove the creator of the chat room");
        }

        userAndRoomManagementRequest.removeChatRoom(chat_room);
    }

    @RequestMapping(value = "/inviteusertochatroom")
    public void inviteUserToChatRoom(AdminUser invitor, User invited_user, Long chat_room_id) { // TODO: to add the input parameters to identify the chat room and the user
        // TODO: need to define the rule of who can invite user to chat room?
        // TODO: the test of postgreSQL
        ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(chat_room_id);

        invitor.addUserToChatRoom(invited_user, chat_room); // only admin can invite user (is that true?)
        userAndRoomManagementRequest.updateChatRoom(chat_room); // Update the chat room in the database

        // Update the `invited_chat_rooms` field of the invited user, which means the user is invited to the chat room
        invited_user.addInvitedChatRoom(chat_room.getId());
        if (invited_user instanceof AdminUser) { // the method `updateAdminUser` is overloaded
            userAndRoomManagementRequest.updateAdminUser((AdminUser) invited_user);
        } else {
            userAndRoomManagementRequest.updateNormalUser((NormalUser) invited_user);
        }
    }

    @RequestMapping(value = "/removeuserfromchatroom")
    public void removeUserFromChatRoom(AdminUser remover, User removed_user, Long chat_room_id) { // TODO: to add the input parameters to identify the chat room and the user
        // TODO: need to define the rule of who can remove user from chat room?

        // TODO: the test of postgreSQL
        ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(chat_room_id);

        remover.removeUserFromChatRoom(removed_user, chat_room); // only admin can remove user (is that true?)
        userAndRoomManagementRequest.updateChatRoom(chat_room); // update the chat room in the database

        // Update the `invited_chat_rooms` field of the removed user, which means the user is removed from the chat room
        removed_user.removeInvitedChatRoom(chat_room.getId());
        if (removed_user instanceof AdminUser) { // the method `updateAdminUser` is overloaded
            userAndRoomManagementRequest.updateAdminUser((AdminUser) removed_user);
        } else {
            userAndRoomManagementRequest.updateNormalUser((NormalUser) removed_user);
        }
    }
}
