package demo;

import java.util.Arrays;
import java.util.HashSet;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.DMNServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

public class App {
    private static final String URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String USER = "krisv";
    private static final String PASSWORD = "krisv";
    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
    private KieServicesConfiguration conf;
    private KieServicesClient kieServicesClient;
    private static final String containerId = "demo20201110_1.0.0-SNAPSHOT";

    public void initialize() {
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        conf.addExtraClasses(new HashSet<>(Arrays.asList(com.myspace.demo20201110.Person.class)));
        kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
    }

    private void demo() {
        initialize();
        DMNServicesClient dmnClient = kieServicesClient.getServicesClient(DMNServicesClient.class);
        DMNContext dmnContext = dmnClient.newContext();
        dmnContext.set("a person", new com.myspace.demo20201110.Person("John Doe", 47L));
        run(dmnClient, dmnContext);
    }

    private void run(DMNServicesClient dmnClient, DMNContext dmnContext) {
        ServiceResponse<DMNResult> serverResp = dmnClient.evaluateAll(containerId, dmnContext);

        DMNResult dmnResult = serverResp.getResult();

        for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {
            System.out.println("--------------------------------------------");
            System.out.println("Decision name:   " + dr.getDecisionName());
            System.out.println("Decision status: " + dr.getEvaluationStatus());
            System.out.println("Decision result: " + dr.getResult());
        }
    }

    public static void main(String[] args) {
        App a = new App();
        a.demo();
    }
}
