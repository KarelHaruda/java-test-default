package com.etnetera.hr.data;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.assertj.core.util.Lists;

/**
 * Simple data entity describing basic properties of every JavaScript framework.
 * 
 * @author Etnetera
 *
 */
@Entity
public class JavaScriptFramework {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, length = 30)
	private String name;

	//Pro jednoduchou kolekci jako je seznam verz� lze pou��t toto
	@ElementCollection 
	private List<String> versions = Lists.newArrayList();
	//pokud by bylo t�eba o verzi ukl�dat v�ce informac� jako nap�. datum vyd�n� verze atd..
	//pak by se musela pou��t dal�� entita napr. JavaScriptFrameworkVersion kter� by nesla v�echny tyto informace 
	//a zde by bylo mapov�n� pravd�podobn� typu one-to-many na tuto entitu
	
	private Date deprecationDate;
	
	private int hypeLevel;
	
	public JavaScriptFramework() {
	}

	public JavaScriptFramework(String name) {
		this(name, null);
	}

	public JavaScriptFramework(String name, Date depricationDate) {
		this(name, depricationDate, 0);
	}

	public JavaScriptFramework(String name, Date depricationDate, int hypeLevel) {
		this.name = name;
		setDeprecationDate(depricationDate);
		setHypeLevel(hypeLevel);
	}

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

	
	public List<String> getVersions() {
		return versions;
	}
	
	public Date getDeprecationDate() {
		return deprecationDate;
	}
	
	public void setDeprecationDate(Date deprecationDate) {
		this.deprecationDate = deprecationDate;
	}

	public int getHypeLevel() {
		return hypeLevel;
	}
	
	public void setHypeLevel(int hypeLevel) {
		this.hypeLevel = hypeLevel;
	}
	
	@Override
	public String toString() {
		return "JavaScriptFramework [id=" + id + ", name=" + name + "]";
	}
}
