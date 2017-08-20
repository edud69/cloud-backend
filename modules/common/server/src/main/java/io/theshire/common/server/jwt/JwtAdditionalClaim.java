

package io.theshire.common.server.jwt;


public enum JwtAdditionalClaim {

 
  UID("uid"),

 
  TID("tid"),

 
  CTM("ctm");

 
  private final String claimKey;

 
  private JwtAdditionalClaim(final String claimKey) {
    this.claimKey = claimKey;
  }


  @Override
  public String toString() {
    return this.claimKey;
  }

}
