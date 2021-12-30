package org.sid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.stream.Stream;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor @ToString
class Patient{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numTicket;
    private String nom;
    private String prenom;
    private String date;
    private int age;
    private float poids;
    private float temperature;
}

@CrossOrigin(origins = "*")
@RepositoryRestResource
interface PatientRepository extends JpaRepository<Patient,Long>{}

@Projection(name = "p1", types = Patient.class)
interface PatientProjection{
    public Long getNumTicket();
    public String getNom();
    public String getPrenom();
}

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(PatientRepository customerRepository, RepositoryRestConfiguration repositoryRestConfiguration){
        return args -> {
            repositoryRestConfiguration.exposeIdsFor(Patient.class);
            customerRepository.save(new Patient(null, "Ba", "Zal", "2021-12-29", 25, 50, 50));
            customerRepository.save(new Patient(null, "Diallo", "Bassirou", "2021-12-29", 25, 50, 50));
            customerRepository.save(new Patient(null, "Gueye", "Mamadou", "2021-12-29", 25, 50, 50));
            customerRepository.save(new Patient(null, "Barry", "Aliou", "2021-12-29", 100, 45, 78));
            customerRepository.save(new Patient(null, "Camara", "Alpha", "2021-12-29", 36, 78, 32));

            customerRepository.findAll().forEach(System.out::println);
        };
    }

}
