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
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    private UUID uuid;


    private UUID accountUuid;

    private UUID journalUuid;

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
