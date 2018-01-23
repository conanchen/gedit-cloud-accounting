package com.github.conanchen.gedit.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

/**
 * @author hai
 * @description
 * @email hilin2333@gmail.com
 * @date 23/01/2018 10:44 AM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Posting {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(
            name = "uuid",
            strategy = "com.github.conanchen.gedit.accounting.utils.database.CustomUUIDGenerator"
    )
    @Column(columnDefinition = "char(32)")
    private String uuid;

    @Column(columnDefinition = "char(32)")
    private String accountUuid;

    @Column(columnDefinition = "char(32)")
    private String journalUuid;

    /**
     * the unit must be penny(分）
     */
    @Column(columnDefinition = "int(11)")
    private Integer amount;

    @Column(columnDefinition = "datetime")
    private Date createdDate;

    @Column(columnDefinition = "datetime")
    private Date updatedDate;
}
