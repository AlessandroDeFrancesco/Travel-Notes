//
//  AddElementViewController.h
//  Travel Notes
//
//  Created by gianpaolo on 30/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Element.h"
#import "PageViewCell.h"

typedef enum addElementType {CAPTURE_VIDEO, CAPTURE_IMAGE, CHOOSE_VIDEO, CHOOSE_IMAGE, WRITE_NOTE} AddElementType;

@interface AddElementViewController : UIViewController<UICollectionViewDataSource, UICollectionViewDelegateFlowLayout, UIImagePickerControllerDelegate, UINavigationControllerDelegate>{
    UICollectionView *_journalCollectionView;
    PageViewCell *_page;
    UIImagePickerController *_imagePickerController;
    NSData *data;
    @public
    Element *_currentElement;
    AddElementType _addElementType;
}

@property (weak, nonatomic) IBOutlet UIView *pageConatiner;
@property (weak, nonatomic) IBOutlet UIButton *addElementButton;

@end
