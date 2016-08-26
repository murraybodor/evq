package ca.aeso.evq.server.servlet.struts;

import org.apache.struts.action.ActionForm;

public class BaseActionForm extends ActionForm 
{

  private String method;

  public BaseActionForm()
  {
  }


  public void setMethod(String method)
  {
      this.method = method;
  }

  public String getMethod()
  {
      return method;
  }

}