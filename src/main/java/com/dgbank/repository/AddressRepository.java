package com.dgbank.repository;

import com.dgbank.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long>  {
    
    // Trova indirizzi per città
    List<Address> findByCity(String city);
    
    // Trova indirizzi per CAP
    List<Address> findByPostalCode(String postalCode);
    
    // Trova indirizzi per provincia
    List<Address> findByProvince(String province);

}
