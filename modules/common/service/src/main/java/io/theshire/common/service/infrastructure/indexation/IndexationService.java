

package io.theshire.common.service.infrastructure.indexation;

import io.theshire.common.domain.DomainObject;
import io.theshire.common.utils.security.permission.constants.SecurityPermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;


public interface IndexationService {

 
  @PreAuthorize("hasPermission(null, '" + SecurityPermissionConstants.INDEX_ALL + "')")
  void indexAll();

 
  Set<Class<? extends DomainObject>> listIndexableTypes();
  
 
  @PreAuthorize("hasPermission(null, '" + SecurityPermissionConstants.INDEX_DELETE_ALL + "')")
  void deleteAll();

 
  @PreAuthorize("hasPermission(#type, '" + SecurityPermissionConstants.INDEX_ALL_OF_SPECIFIC_TYPE
      + "')")
  <T extends DomainObject> void indexType(final Class<T> type);

}
