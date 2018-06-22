//
//  PopUpDatePickerViewController.m
//  Travel Notes
//
//  Created by ianfire on 09/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "PopUpDatePickerViewController.h"

@interface PopUpDatePickerViewController ()

@end

@implementation PopUpDatePickerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)okButtonclicked:(id)sender {    
    [_createJournalViewController dateChosen: _datePicker.date];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
