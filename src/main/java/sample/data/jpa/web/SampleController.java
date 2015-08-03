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

package sample.data.jpa.web;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sample.data.jpa.domain.City;
import sample.data.jpa.domain.Hotel;
import sample.data.jpa.service.CityService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Controller
public class SampleController {

	Long cityId;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private CityService cityService;

	@RequestMapping("/")
	@ResponseBody
	@Transactional(readOnly = true)
	public String helloWorld() {
		return this.cityService.getCity("Bath", "UK").getName();
	}

	@RequestMapping("/addCity")
	@ResponseBody
	@Transactional(readOnly = true)
	public String addCity() {
		City city = new City("Oxford", "UK");
		city.setState("Wrong state");
		entityManager.persist(city);
		entityManager.flush();
		cityId = city.getId();

		return city.toString();
	}

	@RequestMapping("/changeCity")
	@ResponseBody
	@Transactional(readOnly = true)
	public String changeCity() {
		City updateCity = entityManager.find(City.class, cityId);
		updateCity.setState("Oxfordshire");
		entityManager.persist(updateCity);
		entityManager.flush();

		return this.cityService.getCity("Oxford", "UK").toString();
	}

	@RequestMapping("/showCity")
	@ResponseBody
	@Transactional(readOnly = true)
	public String showRevision() {
		AuditReader reader = AuditReaderFactory.get(entityManager);
		City city_rev1 = reader.find(City.class, cityId, 1);
		City city_rev2 = reader.find(City.class, cityId, 2);
		return "Revision 1: " + city_rev1.toString() + "\n Revision 2: " + city_rev2.toString();
	}

	@RequestMapping("/addHotel")
	@ResponseBody
	@Transactional(readOnly = true)
	public String addHotel() {
		Hotel hotel = new Hotel(cityService.getCity("Bath", "UK"), "Bath hotel");
		entityManager.persist(hotel);
		entityManager.flush();

		return hotel.getName();
	}


}
