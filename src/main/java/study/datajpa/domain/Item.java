package study.datajpa.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Item implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate; //@PrePersist

    public Item(String id) {
        this.id = id;
    }


    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
