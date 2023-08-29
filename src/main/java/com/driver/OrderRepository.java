package com.driver;


import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    HashMap<String, Order> orderHashMap = new HashMap<>();
    HashMap<String,DeliveryPartner> deliveryPartnerHashMap = new HashMap<>();
    HashMap<String, List<Order>> ordersAssignToPartner = new HashMap<>();
    HashMap<String,List<Order>> deleveredOrdersByPartner = new HashMap<>();
    public void addOrder(Order order) {
        orderHashMap.put(order.getId(),order);

    }

    public void addPartner(String partnerId) {
        if (deliveryPartnerHashMap.containsKey(partnerId)){
            return;
        }
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        //we are adding this to avoid null pointer axceptiion
        deliveryPartnerHashMap.put(partnerId,deliveryPartner);
        ordersAssignToPartner.put(partnerId,new ArrayList<>());
        deleveredOrdersByPartner.put(partnerId,new ArrayList<>());
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        Order order = orderHashMap.getOrDefault(orderId,null);
        DeliveryPartner deliveryPartner = deliveryPartnerHashMap.getOrDefault(partnerId,null);

        if (order == null || deliveryPartner == null) return;

        ordersAssignToPartner.getOrDefault(partnerId,new ArrayList<>()).add(order);
        deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()+1);
    }

    public Order getOrderById(String orderId) {
       return orderHashMap.getOrDefault(orderId,null);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerHashMap.getOrDefault(partnerId,null);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
       if (deliveryPartnerHashMap.containsKey(partnerId)){
            DeliveryPartner deliveryPartner = deliveryPartnerHashMap.get(partnerId);
            return deliveryPartner.getNumberOfOrders();
        }
       else return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<Order> orderList = ordersAssignToPartner.getOrDefault(partnerId,new ArrayList<>());
        if (orderList.size() == 0) return new ArrayList<>();
        List<String> order = new ArrayList<>();
        for (Order o : orderList){
            order.add(o.getId());
        }

        return order;
    }

    public List<String> getAllOrders() {
        List<String> allOrders = new ArrayList<>();
        if (orderHashMap.size() == 0) return new ArrayList<>();

       for (String key : orderHashMap.keySet()){
           allOrders.add(key);
       }
       return allOrders;
    }

    public Integer getCountOfUnassignedOrders() {
        Integer totalOrders = orderHashMap.size();
        Integer countofAssignedOredrs = 0;
        for (String id : ordersAssignToPartner.keySet()){
            countofAssignedOredrs += getOrderCountByPartnerId(id);
        }
        return totalOrders-countofAssignedOredrs;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        String hh = time.substring(0,2);
        String mm = time.substring(3);

        int h = Integer.parseInt(hh)*60;
        int m = Integer.parseInt(mm);
        Integer ans = 0;
        int Gtime = h+m;
        Integer getCount = 0;
        List<Order> list = ordersAssignToPartner.getOrDefault(partnerId,new ArrayList<>());
        if (list.size()== 0) return 0;
        List<Order> deleveredOrders = new ArrayList<>();
        for (Order o : list){
          if (o.getDeliveryTime() <= Gtime) {
              getCount++;
              deleveredOrders.add(o);
          }
        }
        if (deleveredOrdersByPartner.containsKey(partnerId)){
            deleveredOrdersByPartner.remove(partnerId);
        }
     deleveredOrdersByPartner.put(partnerId,deleveredOrders);

        ans = list.size() - deleveredOrders.size();

        return ans;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int time = Integer.MIN_VALUE;

        if (deleveredOrdersByPartner.containsKey(partnerId)){
            List<Order> list = deleveredOrdersByPartner.get(partnerId);

            for (Order order : list){
                if (order.getDeliveryTime()>= time){
                    time = order.getDeliveryTime();
                }
            }
        int h = time/60;
            int m = time%60;

            String hh = String.valueOf(h);
            String mm = "";
            if (m >= 0 && m <=9) {
                mm = "0" + String.valueOf(m);
            }
            else {
                mm = String.valueOf(m);
            }

            return hh + ":" + mm;
        }

        List<Order> list = ordersAssignToPartner.getOrDefault(partnerId,new ArrayList<>());
        if (list.size()==0) return "";

        for (Order order : list){
            if (order.getDeliveryTime()>= time){
                time = order.getDeliveryTime();
            }
        }
        int h = time/60;
        int m = time%60;

        String hh = String.valueOf(h);
        String mm = "";
        if (m >= 0 && m <=9) {
            mm = "0" + String.valueOf(m);
        }
        else {
            mm = String.valueOf(m);
        }

        return hh+"" + ":" + mm+"";

    }

    public void deletePartnerById(String partnerId) {
        deliveryPartnerHashMap.remove(partnerId);
        ordersAssignToPartner.remove(partnerId);
        deleveredOrdersByPartner.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderHashMap.remove(orderId);
        for (String partnerId : ordersAssignToPartner.keySet()){
            List<Order> orders = ordersAssignToPartner.get(partnerId);
            for (Order order : orders){
                if (order.getId().equals(orderId)){
                    orders.remove(order);
                }
            }
            ordersAssignToPartner.put(partnerId,orders);
        }
    }
}
