package com.Wsdl2Code.WebServices.CTwalkService;

//------------------------------------------------------------------------------
// <wsdl2code-generated>
//    This code was generated by http://www.wsdl2code.com version  2.6
//
// Date Of Creation: 11/23/2015 2:51:36 PM
//    Please dont change this code, regeneration will override your changes
//</wsdl2code-generated>
//
//------------------------------------------------------------------------------
//
//This source code was auto-generated by Wsdl2Code  Version
//
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class Country implements KvmSerializable {
    
    public int CountryId;
    public boolean CountryIdSpecified;
    public String LocalName;
    public String Name;
    
    public Country(){}
    
    public Country(SoapObject soapObject)
    {
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("CountryId"))
        {
            Object obj = soapObject.getProperty("CountryId");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                CountryId = Integer.parseInt(j.toString());
            }else if (obj!= null && obj instanceof Number){
                CountryId = (Integer) obj;
            }
        }
        if (soapObject.hasProperty("CountryIdSpecified"))
        {
            Object obj = soapObject.getProperty("CountryIdSpecified");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                CountryIdSpecified = Boolean.parseBoolean(j.toString());
            }else if (obj!= null && obj instanceof Boolean){
                CountryIdSpecified = (Boolean) obj;
            }
        }
        if (soapObject.hasProperty("LocalName"))
        {
            Object obj = soapObject.getProperty("LocalName");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                LocalName = j.toString();
            }else if (obj!= null && obj instanceof String){
                LocalName = (String) obj;
            }
        }
        if (soapObject.hasProperty("Name"))
        {
            Object obj = soapObject.getProperty("Name");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                Name = j.toString();
            }else if (obj!= null && obj instanceof String){
                Name = (String) obj;
            }
        }
    }
    @Override
    public Object getProperty(int arg0) {
        switch(arg0){
            case 0:
                return CountryId;
            case 1:
                return CountryIdSpecified;
            case 2:
                return LocalName;
            case 3:
                return Name;
        }
        return null;
    }
    
    @Override
    public int getPropertyCount() {
        return 4;
    }
    
    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "CountryId";
                break;
            case 1:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "CountryIdSpecified";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LocalName";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Name";
                break;
        }
    }
    
    @Override
    public String getInnerText() {
        return null;
    }
    
    
    @Override
    public void setInnerText(String s) {
    }
    
    
    @Override
    public void setProperty(int arg0, Object arg1) {
    }
    
}
