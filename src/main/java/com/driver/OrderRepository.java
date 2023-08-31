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
    HashMap<String,String> OrderToPartner = new HashMap<>();
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
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        Order order = orderHashMap.get(orderId);
        DeliveryPartner deliveryPartner = deliveryPartnerHashMap.get(partnerId);

        if (order == null || deliveryPartner == null) return;

        ordersAssignToPartner.getOrDefault(partnerId,new ArrayList<>()).add(order);
        OrderToPartner.put(orderId,partnerId);
        deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()+1);
    }

    public Order getOrderById(String orderId) {
       return orderHashMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerHashMap.get(partnerId);
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
        Integer assigned = OrderToPartner.size();
        return totalOrders-assigned;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {

        String[] arr = time.split(":");
        String hh = arr[0];
        String mm = arr[1];

        int h = Integer.parseInt(hh)*60;
        int m = Integer.parseInt(mm);

        int Gtime = h+m;
        Integer getCount = 0;
        List<Order> list = ordersAssignToPartner.getOrDefault(partnerId,new ArrayList<>());
        if (list.size()== 0) return 0;

        for (Order o : list){
          if (o.getDeliveryTime() > Gtime) {
              getCount++;
          }
        }
        return getCount;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int time = Integer.MIN_VALUE;

      List<Order> list = ordersAssignToPartner.get(partnerId);
            for (Order order : list){
               time = Math.max(time,order.getDeliveryTime());
            }
        int h = time/60;
            int m = time%60;

            String hh = ""+h;
            String mm = ""+m;
            if (hh.length()<2){
                hh = "0"+hh;
            }
            if (mm.length()<2){
                mm = "0"+mm;
            }


            return hh + ":" + mm;

    }

    public void deletePartnerById(String partnerId) {
        List<Order> list = ordersAssignToPartner.get(partnerId);
        for (Order order : list){
            OrderToPartner.remove(order.getId());
        }
      ordersAssignToPartner.remove(partnerId);
        deliveryPartnerHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderHashMap.remove(orderId);
        String partnerId = OrderToPartner.get(orderId);
        OrderToPartner.remove(orderId);

        List<Order> list = ordersAssignToPartner.get(partnerId);
        for (Order o : list){
            if (o.getId().equals(orderId))
                list.remove(o);
        }
        ordersAssignToPartner.put(partnerId,list);

    }
}
