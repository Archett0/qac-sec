package com.team12.answer;

import com.team12.encrypt.AESEncryptionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = AESEncryptionConverter.class)
    private String content;

    private LocalDateTime createdAt;

    private UUID ownerId;

    private UUID questionId;

}
