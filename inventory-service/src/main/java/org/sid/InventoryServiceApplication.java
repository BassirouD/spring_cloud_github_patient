package org.sid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Entity
@Data @AllArgsConstructor @NoArgsConstructor @ToString
class Consultation{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numConsultation;
    private Long numTicket;
    private Date date;
    private String nom;
    private String prenom;
    private int age;
    private Double montant;
}

@Data
class Patient {
    private Long numTicket;
    private String nom;
    private String prenom;
    private int age;
    private float poids;
    private float temperature;
}

@Data
class Paiement {
    private Long numPaiement;
    private Date date;
    private Long numTicket;
    private String nom;
    private String prenom;
    private Double montant;
}

@FeignClient(name = "BILLING-SERVICE")
interface PaiementService {
    @GetMapping("/paiements/{numPaiement}")
    public Paiement findPaiementById(@PathVariable(name = "numPaiement") Long numPaiement);
}

@FeignClient(name = "CUSTOMER-SERVICE")
interface PatientService {
    @GetMapping("/patients/{numTicket}")
    public Patient findPatientById(@PathVariable(name = "numTicket") Long numTicket);
}


@RepositoryRestResource
@CrossOrigin(origins = "*")
interface ConsultationRepository extends JpaRepository<Consultation, Long>{}

@SpringBootApplication
@EnableFeignClients
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ConsultationRepository consultationRepository,
                            RepositoryRestConfiguration repositoryRestConfiguration,
                            PatientService patientService,
                            PaiementService paiementService
                            ){
        return args -> {
            repositoryRestConfiguration.exposeIdsFor(Consultation.class);
        };
    }
}

@RestController
@CrossOrigin(origins = "*")
class ConsultationRestController {
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PaiementService paiementService;

    @PostMapping(value = "/saveConsultation/{numPaiement}/{numTicket}")
    public Consultation saveConsultation(@PathVariable Long numPaiement, @PathVariable Long numTicket, @RequestBody Consultation consultation) {
        Paiement paiement = paiementService.findPaiementById(numPaiement);
        Patient patient = patientService.findPatientById(numTicket);
        consultation.setNumTicket(patient.getNumTicket());
        consultation.setNom(patient.getNom());
        consultation.setPrenom(patient.getPrenom());
        consultation.setAge(patient.getAge());
        consultation.setDate(new Date());
        consultation.setMontant(paiement.getMontant());
        return consultationRepository.save(consultation);
    }

    @PutMapping(value = "/updateConsultation/{numConsultation}/{numPaiement}/{numTicket}")
    public Consultation updateConsultation(@PathVariable Long numConsultation, @PathVariable Long numPaiement, @PathVariable Long numTicket, @RequestBody Consultation consultation) {
        consultation.setNumConsultation(numConsultation);
        Paiement paiement = paiementService.findPaiementById(numPaiement);
        Patient patient = patientService.findPatientById(numTicket);
        consultation.setNumTicket(patient.getNumTicket());
        consultation.setNom(patient.getNom());
        consultation.setPrenom(patient.getPrenom());
        consultation.setAge(patient.getAge());
        consultation.setDate(new Date());
        consultation.setMontant(paiement.getMontant());
        return consultationRepository.save(consultation);
    }
}

