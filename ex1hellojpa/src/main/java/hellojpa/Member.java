package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    // 기간 Period
    @Embedded
    private Period workPeriod;

    // 주소
    @Embedded
    private Address homeAddress;

//    @Column(name = "TEAM_ID")
//    private Long teamId;

     // Member가 N 이고 TEAM이 1 (다대일 관계) 하나의 팀에 여러개의 멤버가 소속되니까 => 여기가 주인, 외래키가 있는곳
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
//    public void changeTeam(Team team) {
//        this.team = team;
//        team.getMembers().add(this); // 연관 관계 편의 메소드 ( 순수객체상태를 고쳐해서 항상 양쪽에 값을 설정하자 )
//    }


//    @Id @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Column(name = "name")
//    private String username;
//
//    private Integer age;
//
//    @Enumerated(EnumType.STRING)
//    private RoleType roleType;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdDate;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;
//
//
//    private LocalDate testLocalDate;
//    private LocalDateTime testLocalDateTime;
//
//
//    @Lob
//    private String description;
//
//    public Member(){
//    }
//
//    public Member(Long id, String username, Integer age, RoleType roleType, Date createdDate, Date lastModifiedDate, String description) {
//        this.id = id;
//        this.username = username;
//        this.age = age;
//        this.roleType = roleType;
//        this.createdDate = createdDate;
//        this.lastModifiedDate = lastModifiedDate;
//        this.description = description;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public Integer getAge() {
//        return age;
//    }
//
//    public void setAge(Integer age) {
//        this.age = age;
//    }
//
//    public RoleType getRoleType() {
//        return roleType;
//    }
//
//    public void setRoleType(RoleType roleType) {
//        this.roleType = roleType;
//    }
//
//    public Date getCreatedDate() {
//        return createdDate;
//    }
//
//    public void setCreatedDate(Date createdDate) {
//        this.createdDate = createdDate;
//    }
//
//    public Date getLastModifiedDate() {
//        return lastModifiedDate;
//    }
//
//    public void setLastModifiedDate(Date lastModifiedDate) {
//        this.lastModifiedDate = lastModifiedDate;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
}
