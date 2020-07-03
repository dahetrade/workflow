package com.xiek.demoactiviti.ctrl;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @GetMapping("/prepare")
    public void prepare() {
        Deployment deployment = repositoryService.createDeployment()//创建一个部署对象
                .name("请假流程")
                .addClasspathResource("processes/myProcess.bpmn20.xml")
                .addClasspathResource("processes/myProcess.png")
                .deploy();
        System.out.println("部署ID："+deployment.getId());
        System.out.println("部署名称："+deployment.getName());
    }

    /**启动流程实例分配任务给个人*/
    @GetMapping("/start")
    public void start() {

        String userKey="PTM";//脑补一下这个是从前台传过来的数据
        String processDefinitionKey ="myProcess";//每一个流程有对应的一个key这个是某一个流程内固定的写在bpmn内的
        HashMap<String, Object> variables=new HashMap<>();
        variables.put("userKey", userKey);//userKey在上文的流程变量中指定了
        variables.put("days", 2);
        variables.put("users", "a");

        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey(processDefinitionKey,variables);

        System.out.println("流程实例ID:"+instance.getId());
        System.out.println("流程定义ID:"+instance.getProcessDefinitionId());
    }

    /**查询当前人的个人任务*/
    @GetMapping("/findTask/{assignee}")
    public void findTask(@PathVariable("assignee") String assignee){
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
                .taskAssignee(assignee)//指定个人任务查询
                .list();
        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
                System.out.println("getOwner:"+task.getOwner());
                System.out.println("getCategory:"+task.getCategory());
                System.out.println("getDescription:"+task.getDescription());
                System.out.println("getFormKey:"+task.getFormKey());
                Map<String, Object> map = task.getProcessVariables();
                for (Map.Entry<String, Object> m : map.entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }
                for (Map.Entry<String, Object> m : task.getTaskLocalVariables().entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }

            }
        }
    }

    /**查询当前人的组任务*/
    @GetMapping("/searchTask/{candidate}")
    public void searchTask(@PathVariable("candidate") String candidate){
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
                .taskCandidateGroup(candidate)//指定组任务查询
                .list();

        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
            }
        }
    }

    @GetMapping("/completeTask/{taskId}")
    public void completeTask(@PathVariable("taskId") String taskId){
        //任务ID
//        String taskId = "6825fdf2-7e04-11e9-a0c6-408d5ccf513c";

        HashMap<String, Object> variables=new HashMap<>();
        variables.put("days", 1);//userKey在上文的流程变量中指定了

        taskService.complete(taskId,variables);
        System.out.println("完成任务：任务ID："+taskId);
    }

    public void completeTask(List<String> list){

        for (String id : list) {
            // 任务id
            taskService.complete (id);
            System.out.println ("处理任务id：" + id);
        }

    }

    /**查询当前人的组任务*/
    @GetMapping("/searchTask")
    public List<String> searchTask(){

        String assignee = "a";
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
//                .taskCandidateUser("ZJ")//指定组任务查询
//                .taskAssignee(assignee)
                .list();
        String taskid ="";
        String instanceId ="";

        List<String> idList = new ArrayList<String>();

        if(list!=null && list.size()>0){
            for(Task task:list){
                idList.add (task.getId ());
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
            }
        }

        return idList;
    }

    @GetMapping("/run")
    public void run() {

        // 1.部署流程
        prepare ();

        // 2.启动一个流程实例
        start ();

        // 3.任务查询
        List list = searchTask ();

        // 4.处理任务
        completeTask (list);
    }


}
