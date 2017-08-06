package com.inossem.print;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
  
@Controller  
public class PrintController {  
	
	@Autowired
	private ZplPrint print;
	
    @RequestMapping(value="/Hello")  
    public String HelloWorld(Model model){  
        model.addAttribute("message","Hello World!!!");  
        return "HelloWorld";  
    }  
    
    
    @ResponseBody
    @RequestMapping(value="/go",method=RequestMethod.POST) 
    public Result<Order> go(@RequestBody Order order){  
    	System.out.println("test");
    	Result<Order> r = new Result<Order>();
    	r.setData(order);
    	r.setCode(Result.SUCCESS);
    	try{
    		print.execute(order);
    	}catch(Exception e){
    		e.printStackTrace();
    		r.setCode(Result.ERROR);
    		r.setMsg("打印报错!");
    	}
    	return r;  
    }  
      
}  