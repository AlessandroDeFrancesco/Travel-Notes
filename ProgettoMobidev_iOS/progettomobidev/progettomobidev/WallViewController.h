//
//  WallViewController.h
//  Travel Notes
//
//  Created by gianpaolo on 03/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//


#import <UIKit/UIKit.h>
#import <MMDrawerController.h>
#import <UIViewController+MMDrawerController.h>
#import "PopupAddElementViewController.h"
#import "DayView.h"
#import <iCarousel.h>

@interface WallViewController : UIViewController<UICollectionViewDataSource, UICollectionViewDelegateFlowLayout, UIPopoverPresentationControllerDelegate, UIScrollViewDelegate,iCarouselDataSource, iCarouselDelegate>{
    
    UICollectionView *_journalCollectionView;
    DayView *_daysView;
    PopupAddElementViewController *_popoverViewController;
}

@property (weak, nonatomic) IBOutlet UIView *daysCollectionContainer;
@property (weak, nonatomic) IBOutlet UIView *journalCollectionContainer;
@property (strong, nonatomic) UIWindow *window;
@property (strong,nonatomic) MMDrawerController *drawerController;
@property (weak, nonatomic) IBOutlet UIView *noElementView;

- (IBAction)unwindToWallViewController: (UIStoryboardSegue*) segue;
@end
