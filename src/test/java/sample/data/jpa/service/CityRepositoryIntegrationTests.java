/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.data.jpa.service;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.data.jpa.SampleDataJpaApplication;
import sample.data.jpa.config.Transactor;
import sample.data.jpa.domain.City;
import sample.data.jpa.revision.UserRevEntity;
import sample.data.jpa.revision.UserRevisionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for {@link CityRepository}.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleDataJpaApplication.class)
@IntegrationTest
public class CityRepositoryIntegrationTests {

	final static String WRONG_STATE = "Wrong State";
	final static String OXFORDSHIRE_STATE = "Oxfordshire";

	@Autowired
	CityRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private Transactor transactor;

	long cityId;

	@Test
	public void testSaveAndUpdateCity(){
		transactor.perform(() -> {
					addCity();
				});
		transactor.perform(() -> {
					updateCity(cityId);
				});

		transactor.perform(() -> {
			checkRevisions(cityId);
		});

	}

	private void addCity(){
		City city = new City("Oxford", "UK");
		city.setState(WRONG_STATE);
		entityManager.persist(city);
		entityManager.flush();
		cityId = city.getId();
	}

	private void updateCity(long cityId) {
		City updateCity = entityManager.find(City.class, cityId);
		updateCity.setState(OXFORDSHIRE_STATE);
		entityManager.persist(updateCity);
		entityManager.flush();
	}

	private void checkRevisions(long cityId){
		AuditReader reader = AuditReaderFactory.get(entityManager);

		City city_rev1 = reader.find(City.class, cityId, 1);
		assertThat(city_rev1.getState(), is(WRONG_STATE));

		City city_rev2 = reader.find(City.class, cityId, 2);
		assertThat(city_rev2.getState(), is(OXFORDSHIRE_STATE));
	}


	@Test
	public void testUserRevisions() throws Exception{
		transactor.perform(() -> {
			addCity();
		});
		transactor.perform(() -> {
			checkUsers();
		});
	}

	private void checkUsers(){
		AuditReader reader = AuditReaderFactory.get(entityManager);
		AuditQuery query = reader.createQuery()
				.forRevisionsOfEntity(City.class, false, false);

		//This return a list of array triplets of changes concerning the specified revision.
		// The array triplet contains the entity, entity revision information and at last the revision type.
		Object[] obj = (Object[]) query.getSingleResult();

		//In this case we want the entity revision information object, which is the second object of the array.
		UserRevEntity userRevEntity = (UserRevEntity) obj[1];

		String user = userRevEntity.getUsername();
		assertThat(user, is(UserRevisionListener.USERNAME));

	}

}
