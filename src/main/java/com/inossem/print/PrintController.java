package com.inossem.print;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
  
@Controller  
public class PrintController {  
    @RequestMapping(value="/Hello")  
    public String HelloWorld(Model model){  
        model.addAttribute("message","Hello World!!!");  
        return "HelloWorld";  
    }  
    
    
    @RequestMapping(value="/go",method=RequestMethod.POST) 
    @ResponseBody
    public Result<Order> go(@RequestBody Order order){  
    	Result<Order> r = new Result<Order>();
    	r.setData(order);
    	r.setCode(Result.SUCCESS);
    	return r;  
    }  
      
}  