//
//  DayView.h
//  Travel Notes
//
//  Created by gianpaolo on 05/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <iCarousel.h>

@interface DayView : iCarousel

@property (weak, nonatomic) IBOutlet UIButton *rightArrow;
@property (weak, nonatomic) IBOutlet UIButton *leftArrow;
@property (weak, nonatomic) IBOutlet UILabel *dayName;
@property (weak, nonatomic) IBOutlet UILabel *day;
@property (weak, nonatomic) IBOutlet UILabel *monthAndYear;

-(void)fillWithDate:(NSDate *)date;
-(void)hideLeftArrow:(BOOL) b;
-(void)hideRightArrow:(BOOL) b;
@end
