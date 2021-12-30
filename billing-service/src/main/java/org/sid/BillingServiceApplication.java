package org.sid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numPaiement;
    private Date date;
    private Long numTicket;
    private String nom;
    private String prenom;
    private Double montant;
}

@Data
class Ticket {
    private Long numTicket;
    private String nom;
    private String prenom;
}

@FeignClient(name = "CUSTOMER-SERVICE")
interface TicketService {
    @GetMapping("/patients/{numTicket}")
    public Ticket findCustomerById(@PathVariable(name = "numTicket") Long numTicket);
}

@Projection(name = "fullBill", types = Paiement.class)
interface PaiementProjection {
    public Long getId();
    public Double getMontant();
}

@RepositoryRestResource
@CrossOrigin(origins = "*")
interface PaiementRepository extends JpaRepository<Paiement, Long> {
}


@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(PaiementRepository paiementRepository,
                            TicketService ticketService, RepositoryRestConfiguration repositoryRestConfiguration) {
        return args -> {
            repositoryRestConfiguration.exposeIdsFor(Paiement.class);
            /*Ticket ticket = ticketService.findCustomerById(1L);
            System.out.println("***************************");
            System.out.println("ID=" + ticket.getNumTicket());
            System.out.println("Nom=" + ticket.getNom());
            System.out.println("Prenom=" + ticket.getPrenom());
            System.out.println("***************************");

            Paiement paiement = paiementRepository.save(new Paiement(null, new Date(), ticket.getNumTicket(), ticket.getNom(), ticket.getPrenom(), 500.0));
//            Product p1 = inventoryService.findProductById(1L);
//            productItemRepositoy.save(new ProductItem(null, p1.getId(), p1.getPrice(), 30, bill1));
//            System.out.println("***************************");
//            System.out.println("Product ID=" + p1.getId());
//            System.out.println("Product Name=" + p1.getName());
//            System.out.println("Product Price=" + p1.getPrice());
//            System.out.println("***************************");
//
//            Product p2 = inventoryService.findProductById(2L);
//            productItemRepositoy.save(new ProductItem(null, p2.getId(), p2.getPrice(), 15, bill1));
//
//            Product p3 = inventoryService.findProductById(3L);
//            productItemRepositoy.save(new ProductItem(null, p3.getId(), p3.getPrice(), 78, bill1));
//
//            productItemRepositoy.findAll().forEach(System.out::println);
            paiementRepository.findAll().forEach(System.out::println);*/
        };
    }
}

@RestController
@CrossOrigin(origins = "*")
class BillRestController {
    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private TicketService ticketService;

    @GetMapping("/fullBill/{id}")
    public Paiement getBill(@PathVariable(name = "id") Long id) {
        Paiement paiement = paiementRepository.findById(id).get();
        return paiement;
    }

    @PostMapping(value = "/savePaiement/{numTicket}")
    public Paiement savePatient(@PathVariable Long numTicket, @RequestBody Paiement paiement) {
        Ticket ticket = ticketService.findCustomerById(numTicket);
        paiement.setNumTicket(ticket.getNumTicket());
        paiement.setNom(ticket.getNom());
        paiement.setPrenom(ticket.getPrenom());
        paiement.setDate(new Date());
        return paiementRepository.save(paiement);
    }

    @PutMapping(value = "/updatePaiement/{numPaiement}/{numTicket}")
    public Paiement updatePatient(@PathVariable Long numPaiement ,@PathVariable Long numTicket, @RequestBody Paiement paiement) {
        paiement.setNumPaiement(numPaiement);
        Ticket ticket = ticketService.findCustomerById(numTicket);
        paiement.setNumTicket(ticket.getNumTicket());
        paiement.setNom(ticket.getNom());
        paiement.setPrenom(ticket.getPrenom());
        paiement.setDate(new Date());
        return paiementRepository.save(paiement);
    }

    @PatchMapping(value = "/updatePaiement2/{numPaiement}/{numTicket}")
    public Paiement updatePatient2(@PathVariable Long numPaiement ,@PathVariable Long numTicket, @RequestBody Paiement paiement) {
        paiement.setNumPaiement(numPaiement);
        Ticket ticket = ticketService.findCustomerById(numTicket);
        paiement.setNumTicket(ticket.getNumTicket());
        paiement.setNom(ticket.getNom());
        paiement.setPrenom(ticket.getPrenom());
        paiement.setDate(new Date());
        return paiementRepository.save(paiement);
    }
}
