package com.project.echoproject.controller;

import com.project.echoproject.dto.CouponDTO;
import com.project.echoproject.entity.Coupon;
import com.project.echoproject.entity.Point;
import com.project.echoproject.entity.SiteUser;
import com.project.echoproject.entity.UserCoupon;
import com.project.echoproject.service.CouponService;
import com.project.echoproject.service.PointService;
import com.project.echoproject.service.SiteUserService;
import com.project.echoproject.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/mall")
@Controller
@RequiredArgsConstructor
public class MallController {
    private final CouponService couponService;
    private final SiteUserService siteUserService;
    private final UserCouponService userCouponService;
    private final PointService pointService;

    @GetMapping("")
    public String mallHome(Model model) {
        List<Coupon> couponList = this.couponService.getList();

        model.addAttribute("couponList", couponList);
        return "mall";
    }

    @GetMapping("/buy/{id}")
    public String buyCoupon(@PathVariable Long id, Model model, Principal principal) {
        Coupon coupon = couponService.getCoupon(id);
        model.addAttribute("coupon", coupon);

        SiteUser siteUser = siteUserService.findByUserId(principal.getName());

        CouponDTO buyCoupon = new CouponDTO();
        buyCoupon.setCouponId(coupon.getCouponId());
        buyCoupon.setCouponPoint(coupon.getCouponPoint());
        buyCoupon.setCouponName(coupon.getCouponName());
        buyCoupon.setCurrentPoint(siteUser.getCurrentPoint());
        buyCoupon.setBalance(0L);

        model.addAttribute("buyCoupon", buyCoupon);
        return "buy";
    }

//    @GetMapping("/pay")
//    public String payCoupon(@RequestParam Long id, Model model, Principal principal) {
//        Coupon coupon = couponService.getCoupon(id);
//        model.addAttribute("coupon", coupon);
//
//        SiteUser siteUser = siteUserService.findByUserId(principal.getName());
//
//        Long balance = siteUser.getCurrentPoint() - coupon.getCouponPoint();
//
//        if (balance < 0) {
//            return "error/payment_failed";
//        } else {
//            // 포인트 차감
//            SiteUser updateUser = siteUserService.buyCoupon(siteUser.getUserId(), balance);
//
//            // 포인트내역 insert
//            LocalDateTime now = LocalDateTime.now();
//            Point addPoint = new Point();
//            addPoint.setUserId(updateUser);
//            addPoint.setPointInfo("coupon");
//            addPoint.setPoint(coupon.getCouponPoint());
//            addPoint.setInsertDate(now);
//            pointService.addPointHistory(addPoint);
//
//            // 쿠폰내역 insert
//            UserCoupon addCoupon = new UserCoupon();
//            addCoupon.setUserId(updateUser);
//            addCoupon.setCouponId(coupon);
//            addCoupon.setInsertDate(now);
//            userCouponService.addCoupon(addCoupon);
//
//            model.addAttribute("updateUser", updateUser);
//            return "pay";
//        }
//
//    }

    @PostMapping("/buy/{id}")
    public String Coupon(@PathVariable Long id, Model model, Principal principal) {
        Coupon coupon = couponService.getCoupon(id);
        model.addAttribute("coupon", coupon);

        SiteUser siteUser = siteUserService.findByUserId(principal.getName());

        Long balance = siteUser.getCurrentPoint() - coupon.getCouponPoint();

        if (balance < 0) {
            return "error/payment_failed";
        } else {
            // 포인트 차감
            SiteUser updateUser = siteUserService.buyCoupon(siteUser.getUserId(), balance);

            // 포인트내역 insert
            LocalDateTime now = LocalDateTime.now();
            Point addPoint = new Point();
            addPoint.setSiteUser(updateUser);
            addPoint.setPointInfo("coupon");
            addPoint.setPoint(coupon.getCouponPoint());
            addPoint.setInsertDate(now);
            pointService.addPointHistory(addPoint);

            // 쿠폰내역 insert
            UserCoupon addCoupon = new UserCoupon();
            addCoupon.setUserId(updateUser);
            addCoupon.setCoupon(coupon);
            addCoupon.setInsertDate(now);
            userCouponService.addCoupon(addCoupon);

            model.addAttribute("updateUser", updateUser);
            return "pay";
        }
    }
}
