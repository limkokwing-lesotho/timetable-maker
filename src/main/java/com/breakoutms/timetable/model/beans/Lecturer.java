package com.breakoutms.timetable.model.beans;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Data @NoArgsConstructor
public class Lecturer implements Serializable {

	@Serial
	private static final long serialVersionUID = -7781666121955606948L;
	@Id @GeneratedValue
	private Integer id;

	@EqualsAndHashCode.Exclude
	private String title;

	@Column(unique = true)
	@NotBlank
	private String name;

	public Lecturer(String title, String name){
		this.title = title;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
