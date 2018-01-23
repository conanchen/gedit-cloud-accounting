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

/**
 * @author hai
 * @description
 * @email hilin2333@gmail.com
 * @date 23/01/2018 10:42 AM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Journal {
    @Id
    @GeneratedValue(generator = "custom-uuid")
    @GenericGenerator(
            name = "custom-uuid",
            strategy = "com.github.conanchen.gedit.accounting.utils.database.CustomUUIDGenerator"
    )
    @Column(columnDefinition = "char(32)")
    private String uuid;

    /**
     *  such as deposit（存款）、withdraw（提现）、 transfer（交易）
     */
    @Column(columnDefinition = "varchar(16)")
    private String journalType;

    @Column(columnDefinition = "datetime")
    private Date createdDate;

    @Column(columnDefinition = "datetime")
    private Date updatedDate;
}
