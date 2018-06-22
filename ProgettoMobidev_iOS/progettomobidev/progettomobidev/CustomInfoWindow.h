//
//  CustomInfoWindow.h
//  Travel Notes
//
//  Created by gianpaolo on 02/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CustomInfoWindow : UIView

@property (weak,nonatomic) IBOutlet UILabel *nameLabel;
@property (weak,nonatomic) IBOutlet UILabel *dateLabel;
@property (weak,nonatomic) IBOutlet UILabel *hourLabel;
@property (weak,nonatomic) IBOutlet UITextView *textContentView;
@property (weak,nonatomic) IBOutlet UIImageView *imageContentView;
@end
