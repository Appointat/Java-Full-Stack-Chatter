package full.stack.chatter.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "Messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "sender_id")
    private Long sender_id;

    @Column(name = "chat_room_id")
    private Long chat_room_id;

    @Column(name = "created_date")
    private LocalDateTime created_date;

    public Message() {
    }

    public Message(Long id, String message, Long sender_id, Long chat_room_id, LocalDateTime created_date) {
        this.id = id;
        this.message = message;
        this.sender_id = sender_id;
        this.chat_room_id = chat_room_id;
        this.created_date = created_date;
    }

    public Long getId() {
        return this.id;
    }

    public String getMessage() {
        return this.message;
    }

    public Long getSender_id() {
        return this.sender_id;
    }

}
