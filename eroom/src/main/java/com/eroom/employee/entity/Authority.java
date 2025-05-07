package com.eroom.employee.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name="authority")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorityNo;

    @Column(name = "authority_name")
    private String authorityName;

    @ManyToMany(mappedBy="authorities")
    private List<Employee> employees;

    // 권한과 관련된 메뉴 매핑을 가져옵니다
    @OneToMany(mappedBy = "authority")
    private List<AuthorityMenuMapping> authorityMenuMappings;
}
