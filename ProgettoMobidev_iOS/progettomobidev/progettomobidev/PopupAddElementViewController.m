//
//  PopupAddElementViewController.m
//  Travel Notes
//
//  Created by ianfire on 02/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "PopupAddElementViewController.h"
#import "AddElementViewController.h"

@interface PopupAddElementViewController ()

@end

@implementation PopupAddElementViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)addElement:(id)sender {
    NSLog(@"segue %@", _wallViewController);
    [_wallViewController performSegueWithIdentifier:@"fromWallToAddElement" sender:sender];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
