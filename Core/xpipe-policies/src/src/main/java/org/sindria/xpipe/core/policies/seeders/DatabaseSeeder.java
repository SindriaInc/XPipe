package org.sindria.xpipe.core.policies.seeders;

import org.sindria.xpipe.core.policies.models.*;
import org.sindria.xpipe.core.policies.repositories.*;

//import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
public class DatabaseSeeder {

    //private Logger logger = Logger.getLogger(DatabaseSeeder.class);
    private final UserRepository userRepository;

    private final TypeRepository typeRepository;

    private final PolicyRepository policyRepository;

    private final ActionRepository actionRepository;

    private final CapabilityRepository capabilityRepository;

    private final ActionCapabilityRepository actionCapabilityRepository;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseSeeder(UserRepository userRepository, TypeRepository typeRepository, PolicyRepository policyRepository, ActionRepository actionRepository, CapabilityRepository capabilityRepository, ActionCapabilityRepository actionCapabilityRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
        this.policyRepository = policyRepository;
        this.actionRepository = actionRepository;
        this.capabilityRepository = capabilityRepository;
        this.actionCapabilityRepository = actionCapabilityRepository;

        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) throws IOException {
        seedUsersTable();
        seedTypesTable();
        seedPoliciesTable();
        seedActionsTable();
        seedCapabilitiesTable();
        seedActionCapabilityTable();
    }


    private void seedUsersTable() {
        String sql = "SELECT uuid FROM users U WHERE U.uuid = \"b2033cea-2ea6-4acd-9c75-2a2fdc638bd9\" LIMIT 1";
        List<User> u = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);
        if(u == null || u.size() <= 0) {
            User user = new User();
            user.setUuid("b2033cea-2ea6-4acd-9c75-2a2fdc638bd9");
            userRepository.save(user);
            System.out.println("Users Seeded");
            //logger.info("Users Seeded");
        } else {
            System.out.println("Users Seeding Not Required");
            //logger.trace("Users Seeding Not Required");
        }
    }

    private void seedTypesTable() throws IOException {

        this.typeRepository.disableForeignCheck();
        this.typeRepository.truncate();
        this.typeRepository.enableForeignCheck();

        List<List> csvData = this.csvParser("/seeders/types.csv", ";");

        for (var row : csvData) {

            String idString = (String) row.get(0);

            // convert id to Long
            Long id = Long.parseLong(idString);

            String name = (String) row.get(1);
            String shortName = (String) row.get(2);

            //String subject = " \" " + name + " \" ";

            //String sql = "SELECT name FROM types T WHERE T.name = " + subject + "  LIMIT 1";
            //List<Type> t = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);

            //System.out.println(t.toString());

            //if (t == null || t.size() <= 0) {
                Type type = new Type();
                type.setName(name);
                type.setShortName(shortName);
                typeRepository.save(type);
                System.out.println("Types Seeded");
                //logger.info("Types Seeded");
            //} else {
            //    System.out.println("Types Seeding Not Required");
            //    //logger.trace("Types Seeding Not Required");
            //}

        }

    }



    private void seedPoliciesTable() throws IOException {

        this.policyRepository.disableForeignCheck();
        this.policyRepository.truncate();
        this.policyRepository.enableForeignCheck();

        List<List> csvData = this.csvParser("/seeders/policies.csv", ";");

        for (var row : csvData) {

            String id = (String) row.get(0);
            String content = (String) row.get(1);
            String name = (String) row.get(2);
            String typeIdString = (String) row.get(3);

            // convert typeId to Long
            Long typeId = Long.parseLong(typeIdString);

            Policy policy = new Policy();
            policy.setContent(content);
            policy.setName(name);
            policy.setTypeId(typeId);

            policyRepository.save(policy);

            System.out.println("Policies Seeded");
        }
    }

    private void seedActionsTable() throws IOException {

        this.actionRepository.disableForeignCheck();
        this.actionRepository.truncate();
        this.actionRepository.enableForeignCheck();

        List<List> csvData = this.csvParser("/seeders/actions.csv", ";");

        for (var row : csvData) {

            String idString = (String) row.get(0);

            // convert id to Long
            Long id = Long.parseLong(idString);

            String name = (String) row.get(1);
            String uri = (String) row.get(2);
            String method = (String) row.get(3);

            Action action = new Action();
            action.setName(name);
            action.setUri(uri);
            action.setMethod(method);
            actionRepository.save(action);

            System.out.println("Actions Seeded");
        }
    }


    private void seedCapabilitiesTable() throws IOException {

        this.capabilityRepository.disableForeignCheck();
        this.capabilityRepository.truncate();
        this.capabilityRepository.enableForeignCheck();

        List<List> csvData = this.csvParser("/seeders/capabilities.csv", ";");

        for (var row : csvData) {

            String idString = (String) row.get(0);

            // convert id to Long
            Long id = Long.parseLong(idString);
            String name = (String) row.get(1);

            Capability capability = new Capability();
            capability.setName(name);
            capabilityRepository.save(capability);

            System.out.println("Capabilities Seeded");
        }
    }


    private void seedActionCapabilityTable() throws IOException {

        this.actionCapabilityRepository.disableForeignCheck();
        this.actionCapabilityRepository.truncate();
        this.actionCapabilityRepository.enableForeignCheck();

        List<List> csvData = this.csvParser("/seeders/action_capability.csv", ";");

        for (var row : csvData) {

            String idString = (String) row.get(0);
            String actionIdString = (String) row.get(1);
            String capabilityIdString = (String) row.get(2);

            String comment = (String) row.get(3);

            // convert id to Long
            Long id = Long.parseLong(idString);
            // convert action_id to Long
            Long action_id = Long.parseLong(actionIdString);
            // convert capability_id to Long
            Long capability_id = Long.parseLong(capabilityIdString);

            ActionCapability actionCapability = new ActionCapability();
            actionCapability.setActionId(action_id);
            actionCapability.setCapabilityId(capability_id);
            actionCapabilityRepository.save(actionCapability);

            System.out.println("Action Capability Seeded");
        }
    }



    private List<List> csvParser(String file, String separator) throws IOException {

        try (
            InputStream is = this.getClass().getResourceAsStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            Stream<String> lines = br.lines();

        ) {
            boolean header = true;
            int rowCounter = 0;

            List<List> records = new ArrayList<>();

            for (String line; (line = br.readLine()) != null;) {
                if (header) {
                    header = false;
                } else {
                    rowCounter ++;
                    String[] items = line.split(separator);
                    List<String> fields = Arrays.asList(items);
                    records.add(fields);
                }
            }
            return records;
            }
        }

}
