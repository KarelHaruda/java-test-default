package com.etnetera.hr.data;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.assertj.core.util.Lists;
import org.springframework.util.StringUtils;

import com.etnetera.hr.exceptions.InvalidObjectException;

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

	//Pro jednoduchou kolekci jako je seznam verzí lze použít toto
	@ElementCollection 
	private List<String> versions = Lists.newArrayList();
	//pokud by bylo třeba o verzi ukládat více informací jako např. datum vydání verze atd..
	//pak by se musela použít další entita napr. JavaScriptFrameworkVersion která by nesla všechny tyto informace 
	//a zde by bylo mapování pravděpodobně typu one-to-many na tuto entitu
	
	private Date deprecationDate;
	
	//HyepeLevel může být uložen jako číslo a nebo jako Enum 
	//prvně jsem to vyzkoušel s číslem a pak s Enumem.
	//číslo má tu výhodu že ho lze
//	private int hypeLevel;
	private EHypeLevel hypeLevel;
	
	public JavaScriptFramework() {
	}

	public JavaScriptFramework(String name) {
		this(name, null);
	}

	public JavaScriptFramework(String name, Date depricationDate) {
		this(name, depricationDate, EHypeLevel.NONE);
	}

	public JavaScriptFramework(String name, Date depricationDate, EHypeLevel hypeLevel) {
		this.name = name;  
		setDeprecationDate(depricationDate);
		setHypeLevel(hypeLevel);
	}
	
//	public JavaScriptFramework(String name, Date depricationDate, int hypeLevel) {
//		this.name = name;
//		setDeprecationDate(depricationDate);
//		setHypeLevel(hypeLevel);
//	}

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

	public EHypeLevel getHypeLevel() {
		return hypeLevel;
	}
	
	public void setHypeLevel(EHypeLevel hypeLevel) {
		this.hypeLevel = hypeLevel;
	}

	//GET a SET metody pro pripad ze hype level je  integer
//	public int getHypeLevel() {
//		return hypeLevel;
//	}
//	
//	public void setHypeLevel(int hypeLevel) {
//		this.hypeLevel = hypeLevel;
//	}
	
	//protected metoda slouzici k validaci objektu pred jeho ulozenim 
	//pripadni potomci mohou pridavat validaci vlastnich fieldu
	protected void doValidate(ValidationResult result) {
		if (StringUtils.isEmpty(name)) {
			result.addValidationError("name", "NotEmpty");
		} else if (name.length() > 30) {
			result.addValidationError("name", "Size");			
		}
	}
	
	//metoda slouzi k overeni inegrity objektu (validace) pred jeho perzistenci
	@PreUpdate
    @PrePersist
    private void validate() throws InvalidObjectException{
		ValidationResult result = new ValidationResult();
		doValidate(result);
		if (!result.isValid()) {
			throw new InvalidObjectException(result);
		}
	}

	public boolean assing(JavaScriptFramework source) {
		if (source != null) {
			this.setName(source.getName());
			this.setHypeLevel(source.getHypeLevel());
			this.setDeprecationDate(source.getDeprecationDate());
			this.getVersions().clear();
			this.getVersions().addAll(source.getVersions());
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "JavaScriptFramework [id=" + id + ", name=" + name + "]";
	}
}
