package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    HashMap<String,Order> orderHashMap= new HashMap<>();
    HashMap<String,DeliveryPartner> deliveryPartnerHashMap=new HashMap<>();
    HashMap<String,List<String>> listHashMap = new HashMap<>();
    HashMap<String,String> assignedOrderMap = new HashMap<>();
    public void addOrder(Order order) {
        String orderID= order.getId();
        orderHashMap.put(orderID,order);
    }

    public void addPartner(String partnerId) {
        deliveryPartnerHashMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(orderHashMap.containsKey(orderId) && deliveryPartnerHashMap.containsKey(partnerId))
        {
            List<String> orderList=listHashMap.getOrDefault(partnerId,new ArrayList<>());
            orderList.add(orderId);
            listHashMap.put(partnerId,orderList);
        }
        deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(deliveryPartnerHashMap.get(partnerId).getNumberOfOrders()+1);
        //Now order has been assigned
        assignedOrderMap.put(orderId, partnerId);
    }

    public Order getOrderById(String orderId) {

        return orderHashMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerHashMap.get(partnerId);
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return listHashMap.get(partnerId);
    }

    public List<String> getAllOrders() {

        return new ArrayList<>(orderHashMap.keySet());
    }

    public Integer getCountOfUnassignedOrders() {
        return orderHashMap.size()-assignedOrderMap.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId) {
        int count = 0;

        List<String> ordersToCheck = listHashMap.get(partnerId);
        for(String orderID: ordersToCheck){
            if(orderHashMap.get(orderID).getDeliveryTime()>time){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        List<String> orderList = listHashMap.getOrDefault(partnerId,new ArrayList<>());
        int last=Integer.MIN_VALUE;
        if (!orderList.isEmpty()) {
            for(String order : orderList)
            {
                int time =orderHashMap.get(order).getDeliveryTime();
                last = Math.max(time,last);
            }
        }
        int deliveryTimeInMinutes = last;
        int hours = deliveryTimeInMinutes / 60;
        int minutes = deliveryTimeInMinutes % 60;

        String HH = ""+hours;
        String MM = ""+minutes;

        if(HH.length()==1){
            HH = '0'+HH;
        }
        if(MM.length()==1){
            MM = '0'+MM;
        }
        return HH+':'+MM;
    }

    public void deletePartnerById(String partnerId) {
        List<String> orders = listHashMap.get(partnerId);

        for(String order : orders)
        {
            assignedOrderMap.remove(order);
        }
        deliveryPartnerHashMap.remove(partnerId);
        listHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {

        orderHashMap.remove(orderId);
        String partnerId = assignedOrderMap.get(orderId);
        listHashMap.get(partnerId).remove(orderId);
        deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(deliveryPartnerHashMap.get(partnerId).getNumberOfOrders()-1);
        assignedOrderMap.remove(orderId);
    }
    public Integer getOrderCountByPartnerId(String partnerId) {
        return deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
    }
}