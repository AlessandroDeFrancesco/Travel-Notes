//
//  FriendsTableViewCell.m
//  Travel Notes
//
//  Created by gianpaolo on 26/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "FriendsTableViewCell.h"

@implementation FriendsTableViewCell

@synthesize check;

- (void)awakeFromNib {
    [super awakeFromNib];
    check = NO;
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)checkBoxButton:(id)sender {
    if(!check){
        [[self checkBox] setImage:[UIImage imageNamed:@"CheckBox Marked"] forState:UIControlStateNormal];
        check = YES;
    } else if (check) {
        [[self checkBox] setImage:[UIImage imageNamed:@"CheckBox Empty"] forState:UIControlStateNormal];
        check = NO;
    }
}
@end
