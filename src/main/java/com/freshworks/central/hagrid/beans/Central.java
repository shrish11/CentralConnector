package com.freshworks.central.hagrid.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freshworks.core.processor.AbstractBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Central extends AbstractBean {

    public Central() {

    }

   @Override
      public void transform() {
        // TODO Auto-generated method stub
        System.out.println("transformed");
      }
}
