package com.devumut.DearDiary.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_diaries")
public class DiaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID diary_id;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date diary_date;

    @Column(nullable = false)
    private String diary_content;

    @Column(nullable = false)
    private int diary_emotion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
