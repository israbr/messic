package org.messic.server.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING, length = 31)
public class MDOUser implements MDO,Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -295966654597222940L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USERS")
    @SequenceGenerator(name = "SEQ_USERS", sequenceName = "SEQ_USERS")
    @Column(name = "SID", nullable = false, unique = true)
    private Long sid;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "LOGIN", nullable = false)
    private String login;
    
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    
    @Column(name = "ADMINISTRATOR" , nullable = false)
    private Boolean administrator;

    /**
     * @constructor
     */
    public MDOUser() {
        super();
    }
    
    public MDOUser(String name, String login, String password, Boolean administrator) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.administrator = administrator;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Boolean administrator) {
        this.administrator = administrator;
    }
    
}