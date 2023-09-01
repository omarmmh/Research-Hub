package com.webapp.researchhub.domain;

import jakarta.persistence.*;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class PasswordResetToken {
    // 24 Hour expiry time on the token.
    public static final int EXPIRY_DURATION_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String payload;

    @OneToOne
    private MyUser user;

    private Date expiryDate;

    public  PasswordResetToken() {}
    public PasswordResetToken(MyUser user)
    {
        this.payload = UUID.randomUUID().toString();
        this.user = user;
        setExpiryDate();
    }

    /**
     * Get the token expiry date
     * @return : expiry date of the current token.
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Get the token payload
     * @return : A uuid string.
     */
    public String getPayload() { return payload; }

    /**
     * Get the User that is associated with this token
     * @return : MyUser user who owns this token.
     */
    public MyUser getUser() { return user; }

    /**
     * Set expiry data a specified time in the future.
     */
    private void setExpiryDate()
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, EXPIRY_DURATION_HOURS);
        expiryDate = new Date(cal.getTime().getTime());
    }

    /**
     * Get the ID of this token.
     * @return : Token ID
     */
    public Long getId() {
        return this.id;
    }


}
