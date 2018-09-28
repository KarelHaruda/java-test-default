package com.etnetera.hr;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

		mockMvc.perform(get("/frameworks")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")))
				// Zde si nejsem úplně jistý jestli takto otestováno stačí. Jde totiž o to že
				// jsonPath("$[0].hypeLevel" přečte String a tudíž to nelze porovnávat na
				// EHypeLevel
				// proto se zde porovnává na EHypeLevel.XXXX.name() což je vlastně to co je v té
				// JSON odpovědi, kterou zde testujeme uloženo
				// pravděpodobně by se nechal vytořit správný Enum ze stringu z použitím
				// Enum.valueOf(EHypeLevel.class, text_z_Json),
				// ale pro účely testování by porovnání těch dvou stringů mělo být plně
				// dostačující
				.andExpect(jsonPath("$[0].hypeLevel", is(EHypeLevel.MEDIUM.name())))

				// Toto je test pro případ že by byl HypeLevel integer
//				.andExpect(jsonPath("$[0].hypeLevel",  is(50)))

				.andExpect(jsonPath("$[0].versions", hasSize(2)))
				.andExpect(jsonPath("$[0].versions[0]", is("0.9RC-1")))
				.andExpect(jsonPath("$[0].versions[1]", is("1.0")))

				// Stejná situace jako u EHypeLevel - porovnávání probíhí jako String přečtený z
				// JSON. Proto je to zde porovnáváno na námi očekávaný string.
				// Jinak by se musel string parsovat do Timestamp a pak dále porovnávat proti
				// Timestamp.
				// Opět si myslím že pro účel unit testů je toto dostačující aby to odhalilo
				// pžípadnou zavlečenou chybu
				.andExpect(jsonPath("$[0].deprecationDate", is("2020-11-01T00:00:00.000+0000")))
				// test na počet verzí a jejich hodnoty
				.andExpect(jsonPath("$[1].versions", hasSize(1)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].hypeLevel", is(EHypeLevel.NONE.name())))
				.andExpect(jsonPath("$[1].deprecationDate", nullValue()))
				.andExpect(jsonPath("$[1].versions[0]", is("2018-01-01")))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));

		//Test pridani noveho frameworku
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(2030, 0, 1, 0, 0, 0);
		JavaScriptFramework framework = new JavaScriptFramework("JQuery", cal.getTime(), EHypeLevel.LOW);
		framework.getVersions().add("1.0");
		framework.getVersions().add("2.0");
		framework.getVersions().add("3.0");
		mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.name", is("JQuery")))
				.andExpect(jsonPath("$.hypeLevel", is(EHypeLevel.LOW.name())))
				.andExpect(jsonPath("$.versions", hasSize(3)))
				.andExpect(jsonPath("$.versions[0]", is("1.0")))
				.andExpect(jsonPath("$.versions[1]", is("2.0")))
				.andExpect(jsonPath("$.versions[2]", is("3.0")))
				.andExpect(jsonPath("$.deprecationDate", is("2030-01-01T00:00:00.000+0000")));

		// Test nacteni frameworku s ID 3 - OK
		mockMvc.perform(get("/frameworks/3").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.name", is("JQuery")))
				.andExpect(jsonPath("$.hypeLevel", is(EHypeLevel.LOW.name())))
				.andExpect(jsonPath("$.versions", hasSize(3)))
				.andExpect(jsonPath("$.versions[0]", is("1.0")))
				.andExpect(jsonPath("$.versions[1]", is("2.0")))
				.andExpect(jsonPath("$.versions[2]", is("3.0")))
				.andExpect(jsonPath("$.deprecationDate", is("2030-01-01T00:00:00.000+0000")));

		// Test nacteni frameworku s ID 4 - NOT FOUND
		// Nenalezeno
		mockMvc.perform(get("/frameworks/4").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound());

		// Test vyhledani frameworku podle nazvu - OK
		mockMvc.perform(get("/search/JQuery").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(3)))
				.andExpect(jsonPath("$[0].name", is("JQuery")))
				.andExpect(jsonPath("$[0].hypeLevel", is(EHypeLevel.LOW.name())))
				.andExpect(jsonPath("$[0].versions", hasSize(3)))
				.andExpect(jsonPath("$[0].versions[0]", is("1.0")))
				.andExpect(jsonPath("$[0].versions[1]", is("2.0")))
				.andExpect(jsonPath("$[0].versions[2]", is("3.0")))
				.andExpect(jsonPath("$[0].deprecationDate", is("2030-01-01T00:00:00.000+0000")));

		// Test updatu - OK
		framework.setHypeLevel(EHypeLevel.NONSENSICAL);
		mockMvc.perform(put("/frameworks/3").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.name", is("JQuery")))
				.andExpect(jsonPath("$.hypeLevel", is(EHypeLevel.NONSENSICAL.name())))
				.andExpect(jsonPath("$.versions", hasSize(3)))
				.andExpect(jsonPath("$.versions[0]", is("1.0")))
				.andExpect(jsonPath("$.versions[1]", is("2.0")))
				.andExpect(jsonPath("$.versions[2]", is("3.0")))
				.andExpect(jsonPath("$.deprecationDate", is("2030-01-01T00:00:00.000+0000")));

		// Test smazani - OK
		mockMvc.perform(delete("/frameworks/3").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk());

		// Po smazani uz tam nesmi byt zadny zaznam JQuery
		mockMvc.perform(get("/search/JQuery").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void addFrameworkInvalid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(
				post("/add").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				// Request na url /add neskončil na BadRequest (HTTP status code 400)
				// ale skončil kodem 404 NOT FOUND
				// což způsoboivalo že tento unit test celý skončil chybou už zde na kontrole
				// status().isBadRequest()
				// Bylo nutné doimplementovat obsluhu REST endpointu /add a v dále zařídit
				// validaci objektu před uložením a
				// případné validační chyby předat jako objekt do odpovědi aby se to zde správně
				// otestovalo
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));

		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(framework)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("Size")));
	}
}
