

package io.theshire.admin.endpoint.mvc.tenant;

import io.theshire.admin.domain.tenant.TenantStateEntry;
import io.theshire.admin.endpoint.mvc.LoadBalancedRequestContextResolver;
import io.theshire.admin.service.microservices.MicroServicesListingService;
import io.theshire.admin.service.tenant.TenantAddService;
import io.theshire.admin.service.tenant.TenantRemoveService;
import io.theshire.admin.service.tenant.TenantStateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/tenant")
public class TenantManagementWebMvcController {

 
  @Autowired
  private LoadBalancedRequestContextResolver loadBalancedRequestContextResolver;

 
  @Autowired
  private TenantStateService tenantStateService;

 
  @Autowired
  private MicroServicesListingService microServicesListingService;

 
  @Autowired
  private TenantAddService tenantAddService;

 
  @Autowired
  private TenantRemoveService tenantRemoveService;

 
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public ModelAndView list(final HttpServletRequest request) {
    final ModelAndView mav = new ModelAndView("/tenant-management/listing", "requestContextPath",
        getLoadBalancedContextUrl(request));
    final List<String> orderedMiscroServiceNames = microServicesListingService.listMicroServices()
        .stream().map(ms -> ms.getServiceName()).collect(Collectors.toList());
    mav.addObject("tenantStates", buildTenantStateMatrix(orderedMiscroServiceNames));
    mav.addObject("microServices", orderedMiscroServiceNames);
    return mav;
  }

 
  @RequestMapping(value = "/add", method = RequestMethod.POST)
  public ModelAndView add(final HttpServletRequest request,
      @RequestParam("tenantId") final String tenantId) {
    try {
      tenantAddService.addNewTenant(tenantId);
      ModelAndView mav = list(request);
      mav.addObject("success", true);
      return mav;
    } catch (IllegalArgumentException e) {
      ModelAndView mav = list(request);
      mav.addObject("error", e.getMessage());
      return mav;
    }
  }

 
  @RequestMapping(value = "/remove", method = RequestMethod.POST)
  public ModelAndView remove(final HttpServletRequest request,
      @RequestParam("tenant") final String tenant) {
    tenantRemoveService.removeTenant(tenant);
    return list(request);
  }

 
  @RequestMapping(value = "/initialize", method = RequestMethod.POST)
  public ModelAndView initialize(final HttpServletRequest request,
      @RequestParam("tenant") final String tenantId,
      @RequestParam("service") final String serviceName) {
    tenantAddService.addNewTenant(tenantId, serviceName);
    return list(request);
  }

 
  public List<List<String>> buildTenantStateMatrix(final List<String> orderedMiscroServiceNames) {
    final List<List<String>> matrix = new ArrayList<>();
    final Map<String, Map<String, String>> states = new HashMap<>();
    final Set<TenantStateEntry> tEntry = tenantStateService.getTenantStates();

    orderedMiscroServiceNames.forEach(ms -> {
      tEntry.forEach(t -> {
        Map<String, String> currentStates = states.get(t.getTenantId());
        if (currentStates == null) {
          currentStates = new HashMap<>();
          states.put(t.getTenantId(), currentStates);
        }

        // update current state for the given microservice
        if (t.getActiveSqlMicroservices().stream().filter(activeMs -> activeMs.equals(ms)).findAny()
            .isPresent()) {
          currentStates.put(ms, "CONFIGURED");
        } else {
          currentStates.put(ms, "UNCONFIGURED");
        }
      });
    });

    states.forEach((key, val) -> {
      final List<String> tenantStates = new ArrayList<>();
      tenantStates.add(key);
      orderedMiscroServiceNames.forEach(ms -> tenantStates.add(val.get(ms)));
      matrix.add(tenantStates);
    });

    return matrix;
  }

 
  private String getLoadBalancedContextUrl(final HttpServletRequest request) {
    return loadBalancedRequestContextResolver.getLoadBalancedContextUrl(request);
  }

}
