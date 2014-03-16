package com.junbo.sharding.test.data.dao;

import com.junbo.sharding.annotations.SeedId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by haomin on 14-3-4.
 */
@Entity
@Table(name = "id")
public class ShardEntity {
    @Id
    @SeedId
    @Column(name = "id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long key) {
        this.id = key;
    }
}
