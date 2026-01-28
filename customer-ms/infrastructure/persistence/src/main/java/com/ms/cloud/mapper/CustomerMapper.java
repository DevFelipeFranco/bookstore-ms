package com.ms.cloud.mapper;

import com.ms.cloud.entity.CustomerEntity;
import com.ms.cloud.entity.PurchaseHistoryEntity;
import com.ms.cloud.model.Customer;
import com.ms.cloud.model.CustomerStatus;
import com.ms.cloud.model.CustomerType;
import com.ms.cloud.model.valueobject.Address;
import com.ms.cloud.model.valueobject.Country;
import com.ms.cloud.model.valueobject.CreditLimit;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;
import com.ms.cloud.model.valueobject.Money;
import com.ms.cloud.model.valueobject.PersonalInfo;
import com.ms.cloud.model.valueobject.PurchaseHistory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

    public CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId().value());

        // Personal Info
        entity.setFirstName(customer.getPersonalInfo().firstName());
        entity.setLastName(customer.getPersonalInfo().lastName());
        entity.setPhoneNumber(customer.getPersonalInfo().phoneNumber());

        // Email
        entity.setEmail(customer.getEmail().value());

        // Address
        entity.setAddressStreet(customer.getAddress().street());
        entity.setAddressCity(customer.getAddress().city());
        entity.setAddressState(customer.getAddress().state());
        entity.setAddressZipCode(customer.getAddress().zipCode());
        entity.setAddressCountry(customer.getAddress().country().name());

        // Credit Limit
        entity.setCreditLimitTotalAmount(customer.getCreditLimit().total().amount());
        entity.setCreditLimitTotalCurrency(customer.getCreditLimit().total().currency());
        entity.setCreditLimitUsedAmount(customer.getCreditLimit().used().amount());
        entity.setCreditLimitUsedCurrency(customer.getCreditLimit().used().currency());

        // Type & Status
        entity.setType(customer.getType().name());
        entity.setStatus(customer.getStatus().name());
        entity.setDeactivationReason(customer.getDeactivationReason());

        // Timestamps
        entity.setCreatedAt(customer.getCreatedAt());
        entity.setUpdatedAt(customer.getUpdatedAt());

        // Purchase History
        List<PurchaseHistoryEntity> historyEntities = customer.getPurchaseHistory().stream()
                .map(h -> toHistoryEntity(h, entity))
                .collect(Collectors.toList());
        entity.setPurchaseHistory(historyEntities);

        return entity;
    }

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        List<PurchaseHistory> history = entity.getPurchaseHistory() != null
                ? entity.getPurchaseHistory().stream().map(this::toHistoryDomain).collect(Collectors.toList())
                : Collections.emptyList();

        return Customer.reconstruct(
                CustomerId.of(entity.getId()),
                PersonalInfo.of(entity.getFirstName(), entity.getLastName(), entity.getPhoneNumber()),
                Email.of(entity.getEmail()),
                Address.of(
                        entity.getAddressStreet(),
                        entity.getAddressCity(),
                        entity.getAddressState(),
                        entity.getAddressZipCode(),
                        Country.valueOf(entity.getAddressCountry())),
                CreditLimit.of(
                        Money.of(entity.getCreditLimitTotalAmount(), entity.getCreditLimitTotalCurrency()),
                        Money.of(entity.getCreditLimitUsedAmount(), entity.getCreditLimitUsedCurrency())),
                CustomerType.valueOf(entity.getType()),
                CustomerStatus.valueOf(entity.getStatus()),
                history,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private PurchaseHistoryEntity toHistoryEntity(PurchaseHistory history, CustomerEntity customer) {
        PurchaseHistoryEntity entity = new PurchaseHistoryEntity();
        entity.setOrderId(history.orderId());
        entity.setAmount(history.amount().amount());
        entity.setCurrency(history.amount().currency());
        entity.setPurchaseDate(history.purchaseDate());
        entity.setCustomer(customer);
        return entity;
    }

    private PurchaseHistory toHistoryDomain(PurchaseHistoryEntity entity) {
        return PurchaseHistory.reconstruct(
                entity.getOrderId(),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getPurchaseDate());
    }
}
