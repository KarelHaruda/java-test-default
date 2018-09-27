package com.etnetera.hr;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.etnetera.hr.data.EHypeLevel;
import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Class used for Spring Boot/MVC based tests.
 * 
 * @author Etnetera
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JavaScriptFrameworkTests {

	@Autowired
	private MockMvc mockMvc;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private JavaScriptFrameworkRepository repository;

	private void prepareData() throws Exception {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(2020, 10, 1, 0, 0, 0);
//		JavaScriptFramework react = new JavaScriptFramework("ReactJS", cal.getTime(), 50);
		JavaScriptFramework react = new JavaScriptFramework("ReactJS", cal.getTime(), EHypeLevel.MEDIUM);
		react.getVersions().add("0.9RC-1");
		react.getVersions().add("1.0");
		repository.save(react);

		JavaScriptFramework vue = new JavaScriptFramework("Vue.js");
		vue.getVersions().add("2018-01-01");
		repository.save(vue);
	}

	@Test
	public void frameworksTest() throws Exception {
		prepareData();
		
		mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].versions", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")))
				//Zde si nejsem �pln� jist� jestli takto otestov�no sta��. Jde toti� o to �e jsonPath("$[0].hypeLevel" p�e�te String a tud� to nelze porovn�vat na EHypeLevel
				//proto se zde porovn�v� na EHypeLevel.XXXX.name() co� je vlastn� to co je v t� JSON odpov�di, kterou zde testujeme ulo�eno
				//pravd�podobn� by se nechal vyto�it spr�vn� Enum ze stringu z pou�it�m Enum.valueOf(EHypeLevel.class, name), ale pro ��ely testov�n� by porovn�n� t�ch dvou text� m�lo b�t dosta�uj�c�
				.andExpect(jsonPath("$[0].hypeLevel",  is(EHypeLevel.MEDIUM.name())))
				
				//Toto je test pro p��pad �e by byl HypeLevel integer 
//				.andExpect(jsonPath("$[0].hypeLevel",  is(50)))
				
				//Stejn� situace jako u EHypeLevel - porovn�v�n� prob�h� jako String p�e�ten� z JSON. Proto je to zde porovn�v�no na n�mi o�ek�van� string.
				//Jinak by se musel string parsovat do Timestamp a pak d�le porovn�vat proti Timestamp. Op�t si mysl�m �e pro ��el unit test� je toto dosta�uj�c� aby to odhalilo 
				//p��padnou zavle�enou chybu 
				.andExpect(jsonPath("$[0].deprecationDate",  is("2020-11-01T00:00:00.000+0000")))
				//test na po�et verz�
				.andExpect(jsonPath("$[1].versions", hasSize(1)))
				.andExpect(jsonPath("$[0].versions[0]", is("0.9RC-1")))
				.andExpect(jsonPath("$[0].versions[1]", is("1.0")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].hypeLevel",  is(EHypeLevel.NONE.name())))
				.andExpect(jsonPath("$[1].deprecationDate",  nullValue()))
				.andExpect(jsonPath("$[1].versions[0]", is("2018-01-01")))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));
	}
	
	@Test
	public void addFrameworkInvalid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				//Zde je pravd�podobn� chyba
				//Request na url /add neskon�� na BadRequest (HTTP status code 400) ale skon�� kodem 404 NOT FOUND
				//co� zp�sob� �e tento unit test cel� skon�� chybou u� zde na kontrole status().isBadRequest()
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));
		
		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].message", is("Size")));
		
	}
}
