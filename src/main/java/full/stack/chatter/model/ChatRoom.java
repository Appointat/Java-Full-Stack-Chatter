package full.stack.chatter.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ChatRooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    // Only one of the following two fields will be non-null (admin_creator or normal_creator)
    @ManyToOne
    @JoinColumn(name = "admin_creator")
    private AdminUser admin_creator;

    // Only one of the following two fields will be non-null (admin_creator or normal_creator)
    @ManyToOne
    @JoinColumn(name = "normal_creator")
    private NormalUser normal_creator;

    // Admin users who are added to the chat room to send messages
    // Since the admin users and the normal users are different entities, we need to store them in separate lists
    @ManyToMany
    @JoinTable(
            name = "chat_room_admin_users",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_user_id")
    )
    private List<AdminUser> admin_users_list = new ArrayList<>();

    // Normal users who are added to the chat room to send messages
    // Since the admin users and the normal users are different entities, we need to store them in separate lists
    @ManyToMany
    @JoinTable(
            name = "chat_room_normal_users",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "normal_user_id")
    )
    private List<NormalUser> normal_users_list = new ArrayList<>();

    @Column(name = "duration")
    private int duration; // in seconds

    @Column(name = "created_date")
    private LocalDateTime created_date;

    @Column(name = "expired_date")
    private LocalDateTime expired_date;

    public ChatRoom() {
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public User getCreator() {
        // The mutual exclusivity of admin_creator and normal_creator is already ensured by the validateCreator method
        return this.admin_creator != null ? this.admin_creator : this.normal_creator;
    }

    public int getDuration() {
        return this.duration;
    }

    public List<AdminUser> getAdminUsers() {
        return this.admin_users_list;
    }

    public List<NormalUser> getNormalUsers() {
        return this.normal_users_list;
    }

    public LocalDateTime getCreatedDate() {
        return this.created_date;
    }

    public LocalDateTime getExpiredDate() {
        return this.expired_date;
    }

    public void setChatRoom(String title, String description, User creator, LocalDateTime createDate, LocalDateTime expireDate) {
        this.title = title;
        this.description = description;

        // Set the creator
        if (creator instanceof AdminUser) { // ensure their mutual exclusivity
            this.admin_creator = (AdminUser) creator; // safe to cast
            this.normal_creator = null;
        } else {
            this.normal_creator = (NormalUser) creator; // safe to cast
            this.admin_creator = null;
        }

        this.created_date = createDate;
        this.expired_date = expireDate;
        this.duration = (int) ChronoUnit.SECONDS.between(createDate, expireDate); // duration in seconds
    }

    @PrePersist
    @PreUpdate
    // ChatRoom must have a creator, and it must be either an AdminUser or a NormalUser, but not both
    // Execute this method before persisting or updating the ChatRoom entity
    private void validateCreator() {
        if (this.admin_creator != null && this.normal_creator != null) {
            throw new IllegalStateException("ChatRoom cannot have both AdminUser and NormalUser as creator");
        }
    }

    public void addUser(User user) {
        if (user instanceof AdminUser) {
            if (this.admin_users_list.contains(user)) {
                System.out.println("User already exists in the chat room");
                return;
            }
            this.admin_users_list.add((AdminUser) user);
        } else if (user instanceof NormalUser) {
            if (this.normal_users_list.contains(user)) {
                System.out.println("User already exists in the chat room");
                return;
            }
            this.normal_users_list.add((NormalUser) user);
        } else {
            System.out.println("User must be either AdminUser or NormalUser");
        }
    }

    public void removeUser(User user) {
        if (user instanceof AdminUser) {
            if (!this.admin_users_list.contains(user)) {
                System.out.println("User does not exist in the chat room");
                return;
            }
            this.admin_users_list.remove((AdminUser) user);
        } else if (user instanceof NormalUser) {
            if (!this.normal_users_list.contains(user)) {
                System.out.println("User does not exist in the chat room");
                return;
            }
            this.normal_users_list.remove((NormalUser) user);
        } else {
            System.out.println("User must be either AdminUser or NormalUser");
        }
    }
}
